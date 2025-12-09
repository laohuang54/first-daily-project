package com.fth.controller.FuncController;

import com.fth.dto.Result;
import com.fth.service.impl.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
public class ShopController {
    @Autowired
    private ShopService shopService;

    @GetMapping("/show") //展示商品
    public Result show(){
        //TODO 展示商品
        shopService.show();
        return Result.ok();
    }


}
