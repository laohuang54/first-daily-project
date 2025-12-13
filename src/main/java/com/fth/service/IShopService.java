package com.fth.service;

import com.fth.dto.Result;
import com.fth.dto.ShopDTO;
import com.fth.pojo.Shop;

public interface IShopService {
    Shop show();

    void update(Shop shop);

    Result sell(Integer id);

    Result seckill(Integer id);

    Result add(ShopDTO shopDTO, String imgUrl);

    Result getInfo();
}
