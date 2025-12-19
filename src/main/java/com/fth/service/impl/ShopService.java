package com.fth.service.impl;

import cn.hutool.json.JSONUtil;
import com.fth.dto.Result;

import com.fth.dto.ShopDTO;
import com.fth.dto.ShopInfoDTO;
import com.fth.mapper.ShopMapper;
import com.fth.mapper.UserMapper;
import com.fth.pojo.Shop;
import com.fth.pojo.User;
import com.fth.service.IShopService;
import com.fth.utils.RedisData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.fth.constant.KeysConstant.*;
import static com.fth.constant.RedisConstant.EXPIRE_TIME;

@Slf4j
@Service
public class ShopService implements IShopService {
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    @Qualifier("cacheBuilderThreadPool")
    private ExecutorService cacheBuilderThreadPool;

    private static final AtomicInteger RIP_COUNT = new AtomicInteger(0);

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    private static AtomicInteger LOGIC_COUNT = new AtomicInteger(0);

    private static final String LOCK_KEY="shop:lock:";

    private static final Shop EMPTY_SHOP=new Shop();
    static {
        EMPTY_SHOP.setId(-1);
        EMPTY_SHOP.setShop_name("暂无商品");
    }

    @Override
    public Result getShopDetail(Integer id) {
        log.info("用户查询商品详情:"+id);
        String key=DETAILED_SHOP_KEY+id; //储存
        String lockKey=LOCK_KEY+id;//互斥锁
        String jshop = stringRedisTemplate.opsForValue().get(key);
        RedisData data = JSONUtil.toBean(jshop, RedisData.class);
        //缓存存在
        if (data!=null){
            //过期了
            if(System.currentTimeMillis()>data.getExpireTime()){
                return LogicExpireTimeSolve(id, key, lockKey, data,true);
            }
            //没过期--直接返回
            return Result.ok(data.getData());
        }
        //缓存不存在
        return LogicExpireTimeSolve(id, key, lockKey, data,false);
    }

    private Result LogicExpireTimeSolve(Integer id, String key, String lockKey, RedisData data,Boolean isCacheExist) {

        log.info("逻辑过期调用次数:{}",LOGIC_COUNT.getAndIncrement());
        Boolean tryLock=stringRedisTemplate.opsForValue().setIfAbsent(lockKey,"1",10, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(tryLock)){
            CacheBuild(key,lockKey, id);
            return Result.ok(data.getData());
    }else {
            //没拿到锁--返回旧数据
            log.info("没拿到锁次数:{}",COUNT.getAndIncrement());
            if (isCacheExist){
                return Result.ok(data.getData());
            }else {
                log.info("特殊情况：第一次查询触发缓存击穿");
                // 重新查询（此时缓存很可能已被重建）
                String jshop = stringRedisTemplate.opsForValue().get(key);
                RedisData newData = JSONUtil.toBean(jshop, RedisData.class);
                if (newData != null) {
                    return Result.ok(newData.getData());
                } else {
                    // 重试后仍无缓存 → 返回空对象（防穿透兜底）
                    return Result.ok(EMPTY_SHOP);
                }
            }
        }
    }

    private void CacheBuild(String key, String lockKey,Integer id){
        cacheBuilderThreadPool.submit(()->{

            try {
                //拿到锁--查数据库--重建缓存--释放锁
                log.info("缓存击穿--拿到锁--打到数据库");
                RedisData redisData = new RedisData();
                Shop shop = shopMapper.getById(id);
                if (shop==null){
                    CacheRip(key);//缓存穿透
                    return;
                }


                redisData.setData(shop);
                redisData.setExpireTime(System.currentTimeMillis()+EXPIRE_TIME);
                log.info("新的逻辑过期时间：{}",System.currentTimeMillis()+EXPIRE_TIME);
                String json= JSONUtil.toJsonStr(redisData);
                stringRedisTemplate.opsForValue().set(key,json,2*EXPIRE_TIME,TimeUnit.MILLISECONDS);
            } finally {
                stringRedisTemplate.delete(lockKey);
            }

        });
    }

    private void CacheRip(String key){ //缓存穿透解决
        log.info("缓存穿透触发次数：{}",RIP_COUNT.getAndIncrement());
        String json=JSONUtil.toJsonStr(EMPTY_SHOP);
        stringRedisTemplate.opsForValue().set(key,json,5, TimeUnit.MINUTES);
    }




    @Override
    public Shop show() {
        return shopMapper.show();
    }

    @Override
    public void update(Shop shop) {
        shopMapper.update(shop);
    }

    @Override
    @Transactional
    public Result sell(Integer id) { //购买商品
        // 根据id查询商品信息
        Shop shop=shopMapper.getById(id);
        Integer price=shop.getPrice();
        User user=userService.getUserInfo(id);
        if(user.getScore()<price)
            return Result.fail("积分余额不足");
        // 获取商品名称
        String name=shop.getShop_name();
        // 检查商品状态是否为0（已下架）
        if(shop.getStatus()==0)
            return Result.fail("商品"+name+"已下架");
        // 检查商品库存是否小于1（已售罄）
        if(shop.getStock()<1)
            return Result.fail("商品"+name+"已售罄");
        // 扣减库存和积分
        int result=shopMapper.sell(shop); //库存减1
        if(result==0)
            return Result.fail("购买失败");
        userMapper.updateScore(id,price);
        return Result.ok("购买成功");
    }

    @Override
    public Result seckill(Integer id) {
        Shop shop=shopMapper.getById(id);
        Integer price=shop.getPrice();
        User user=userService.getUserInfo(id);
        if(user.getScore()<price)
            return Result.fail("积分余额不足");
        String name=shop.getShop_name();
        // 检查商品状态是否为0（已下架）
        if(shop.getStatus()==0)
            return Result.fail("商品"+name+"已下架");
        // 检查商品库存是否小于1（已售罄）
        if(shop.getStock()<1)
            return Result.fail("商品"+name+"已售罄");
        int result=shopMapper.seckill(shop); //库存减1
        if(result==0)
            return Result.fail("购买失败");
        userMapper.updateScore(id,price);
        return Result.ok("购买成功");
    }

    @Override
    public Result add(ShopDTO shopDTO, String imgUrl) {
        log.info("添加商品:{}",shopDTO,"// 图片地址:{}",imgUrl);
        shopMapper.add(shopDTO,imgUrl);
        return Result.ok("添加成功");
    }

    @Transactional
    @Override
    public Result getInfo() {
        log.info("获取商品信息");
        String key=SHOP_KEY;
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        if (shopJson!=null) {
            List<Shop> shop=JSONUtil.toList(shopJson,Shop.class);
            return Result.ok(shop);
        }
        List<Shop> info = shopMapper.getInfo();
        String json = JSONUtil.toJsonStr(info);
        //将List<Shop>序列化为字符串 存进Redis中
        stringRedisTemplate.opsForValue().set(key,json);
        return Result.ok(info);
    }

    @Override
    public Result getShopList(ShopInfoDTO shop) {
        log.info("service-条件查询:{}",shop);
        List<Shop> shopList = shopMapper.getShopList(shop);
        log.info("service-查询结果:{}",shopList);
        return Result.ok(shopList);
    }

    @Override
    public Result delete(Integer id) {
        shopMapper.deleteById(id);
        return Result.ok("删除成功");
    }

    @Override
    public Result ban(Integer id) {
        Shop shop = shopMapper.getById(id);
        Integer status = shop.getStatus();
        if(status==1){
            shopMapper.ban(id);
            return Result.ok("下架成功");
        }else{
            shopMapper.unban(id);
            return Result.ok("上架成功");
        }
    }

}
