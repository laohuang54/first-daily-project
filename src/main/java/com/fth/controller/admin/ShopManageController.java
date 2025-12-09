package com.fth.controller.admin;

import com.fth.dto.Result;
import com.fth.pojo.Shop;
import com.fth.service.impl.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shop")
public class ShopManageController {
    @Autowired
    private ShopService shopService;
    @PutMapping("/update")
    public Result update(@RequestBody Shop shop){
        shopService.update(shop);
        return Result.ok();
    }
}
