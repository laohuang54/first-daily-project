package com.fth.mapper;

import com.fth.pojo.Shop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ShopMapper {
    @Select("select * from shop where id = 1")
    Shop show();

    void update(Shop shop);
}
