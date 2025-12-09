package com.fth.service.impl;

import com.fth.mapper.ShopMapper;
import com.fth.pojo.Shop;
import com.fth.service.IShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopService implements IShopService {
    @Autowired
    private ShopMapper shopMapper;
    @Override
    public Shop show() {
        return shopMapper.show();
    }

    @Override
    public void update(Shop shop) {
        shopMapper.update(shop);
    }
}
