package com.fth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDTO {
    private Integer id;

    private String name;

    private Integer price;

    private Integer stock;

    private Integer status;
}
