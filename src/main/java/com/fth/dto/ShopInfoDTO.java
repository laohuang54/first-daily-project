package com.fth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopInfoDTO {//用于用户条件查询商品
    private String shop_name;

    private Integer price;

    private LocalDateTime start;

    private Integer stock;

    private LocalDateTime end;

    private List<Integer> categoryIds; // 商品分类

}
