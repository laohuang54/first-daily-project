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

    int sell(Shop shop);

    @Select("select * from shop where id = #{id}")
    Shop getById(Integer id);

    int seckill(Shop shop);
    //void update(Shop shop);
    @Select("select * from shop")
    Shop getInfo();
}
