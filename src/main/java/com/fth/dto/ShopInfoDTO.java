package com.fth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopInfoDTO {
    private String name;

    private Integer price;

    private List<Integer> category_id; // 商品分类
}
