package com.fth.service.impl;

import com.fth.dto.Result;

import com.fth.mapper.ShopMapper;
import com.fth.pojo.Shop;
import com.fth.service.IShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String name=shop.getName();
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
        String name=shop.getName();
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

}
