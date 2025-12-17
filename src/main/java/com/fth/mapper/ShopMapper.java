package com.fth.mapper;


import com.fth.dto.ShopDTO;
import com.fth.dto.ShopInfoDTO;
import com.fth.pojo.Shop;
import org.apache.ibatis.annotations.*;

import java.util.List;

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
    List<Shop> getInfo();

    List<Shop>getShopList(@Param("param")ShopInfoDTO shop);

    void add(ShopDTO shopDTO, String imgUrl);

    @Delete("delete from shop where id = #{id}")
    void deleteById(Integer id);

    @Update("update shop set status = 0 where id = #{id}")
    void ban(Integer id);

    @Update("update shop set status = 1 where id = #{id}")
    void unban(Integer id);
}
