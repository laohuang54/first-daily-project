package com.fth.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shop {
    private Integer id;

    private String name;

    private LocalDateTime create_time; //上架时间

    private LocalDateTime update_time; //更新时间

    private String description; //商品描述

    private String img; //商品图片

    private Integer price; //商品价格

    private Integer stock; //商品库存
}
