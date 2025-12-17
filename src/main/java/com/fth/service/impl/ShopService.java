package com.fth.service.impl;

import com.fth.dto.Result;

import com.fth.dto.ShopDTO;
import com.fth.dto.ShopInfoDTO;
import com.fth.mapper.ShopMapper;
import com.fth.pojo.Shop;
import com.fth.service.IShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fth.constant.KeysConstant.SHOP_KEY;

@Slf4j
@Service
public class ShopService implements IShopService {
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
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
        // 获取商品名称
        String name=shop.getShop_name();
        // 检查商品状态是否为0（已下架）
        if(shop.getStatus()==0)
            return Result.fail("商品"+name+"已下架");
        // 检查商品库存是否小于1（已售罄）
        if(shop.getStock()<1)
            return Result.fail("商品"+name+"已售罄");
        // 扣减库存
        int result=shopMapper.sell(shop); //库存减1
        if(result==0)
            return Result.fail("购买失败");
        return Result.ok("购买成功");
    }

    @Override
    public Result seckill(Integer id) {
        Shop shop=shopMapper.getById(id);
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
        return Result.ok("购买成功");
    }

    @Override
    public Result add(ShopDTO shopDTO, String imgUrl) {
        log.info("添加商品:{}",shopDTO,"// 图片地址:{}",imgUrl);
        shopMapper.add(shopDTO,imgUrl);
        return Result.ok("添加成功");
    }

    @Override
    public Result getInfo() {
        List<Shop> info = shopMapper.getInfo();
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
