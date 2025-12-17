package com.fth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDTO { //用于新增商品
    private String name;

    private Integer price;

    private Integer stock;

    private String description;

    private MultipartFile img; //商品图片
}
