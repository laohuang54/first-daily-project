package com.fth.controller.admin;

import com.fth.dto.BanDTO;
import com.fth.dto.LoginDTO;
import com.fth.dto.Result;
import com.fth.dto.UserDTO;
import com.fth.pojo.Admin;
import com.fth.pojo.User;
import com.fth.properties.JwtProperty;
import com.fth.service.IAdminService;
import com.fth.utils.JwtUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fth.constant.JwtConstant.ADMIN_ID;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private IAdminService adminService;
    @Autowired
    private JwtProperty jwtProperty;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO){
        log.info("管理员登录：{}",loginDTO);
        Admin login = adminService.login(loginDTO);
        //令牌
        Map<String,Object> claims=new HashMap<>();
        claims.put(ADMIN_ID,login.getId());
        String jwt = JwtUtil.createJWT(jwtProperty.getAdminSecretKey(),
                jwtProperty.getAdminTtl(),
                claims
        );
        login.setToken(jwt);
        return Result.ok(login);
    }
    @GetMapping("/users")
    public Result getUser(UserDTO userDTO){
        PageInfo<User> user = adminService.getUser(userDTO);
        return Result.ok(user);
    }
    @GetMapping("/users/{id}")
    public Result getUser(@PathVariable Long id){
        User user = adminService.getUserDetail(id);
        return Result.ok(user);
    }

    @PutMapping("/users/ban")
    public Result banUser(BanDTO banDTO){
        adminService.banUser(banDTO);
        return Result.ok();
    }
}
