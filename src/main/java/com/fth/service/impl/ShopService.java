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
import com.fth.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.fth.constant.KeysConstant.*;
import static java.awt.SystemColor.info;

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

    @Override
    public Result getShopDetail(Integer id) {
        String key=DETAILED_SHOP_KEY+id;
        String jshop = stringRedisTemplate.opsForValue().get(key);
        if(jshop!=null){
            Shop shop=JSONUtil.toBean(jshop,Shop.class);
            return Result.ok(shop);
        }
        Shop shop = shopMapper.getById(id);
        //缓存穿透解决--缓存空值
        //应用场景：查询详细商品时
        if(shop==null){
            stringRedisTemplate.opsForValue().set(key,"缓存穿透给我滚呐!!!",1, TimeUnit.MINUTES);
            return Result.fail("商品不存在");
        }
        String json= JSONUtil.toJsonStr(shop);
        stringRedisTemplate.opsForValue().set(key,json,1,TimeUnit.HOURS);
        return Result.ok(shop);
    }
}
