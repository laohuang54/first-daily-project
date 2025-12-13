package com.fth.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopCategoryRelation {
    private Integer id;

    private Integer shopId; //商品id

    private Integer CategoryId; //分类id
}
