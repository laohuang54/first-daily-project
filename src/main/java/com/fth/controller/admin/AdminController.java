package com.fth.controller.admin;

import com.fth.dto.*;
import com.fth.pojo.Admin;
import com.fth.pojo.Shop;
import com.fth.pojo.User;
import com.fth.properties.JwtProperty;
import com.fth.service.IAdminService;
import com.fth.service.impl.EssayService;
import com.fth.service.impl.ShopService;
import com.fth.utils.JwtUtil;
import com.fth.vo.UserVO;
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
    @Autowired
    private EssayService essayService;


    @Autowired
    private ShopService shopService;
    @PutMapping("/shop/update")
    public Result update(@RequestBody Shop shop){
        shopService.update(shop);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}") //删除用户文章
    public Result deleteEssayAdmin(@PathVariable Integer id) {
        essayService.deleteEssayAdmin(id);
        return Result.ok();
    }

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
/**
 * 获取用户列表接口
 * 通过GET请求方式访问/users路径，接收UserDTO参数并返回用户列表
 *
 * @param userDTO 用户数据传输对象，包含查询条件
 * @return 返回Result对象，其中包含用户列表
 */
    @GetMapping("/users")
    public Result getUser(UserDTO userDTO){  // 定义获取用户列表的方法，接收UserDTO类型的参数
        List<User> user = adminService.getUser(userDTO);  // 调用服务层方法获取用户列表
        return Result.ok(user);  // 返回成功响应，包含用户列表
    }
/**
 * 根据用户ID获取用户信息接口
 * @param id 用户ID，通过路径变量传递
 * @return 返回一个Result对象，包含用户信息
 */
    @GetMapping("/users/{id}")
    public Result getUser(@PathVariable Integer id){  // 使用@PathVariable注解获取路径中的id参数
        UserVO userVO = adminService.getUserDetail(id);  // 调用服务层方法获取用户详细信息
        return Result.ok(userVO);  // 返回成功响应，包含用户信息
    }

/**
 * 禁用用户接口
 * 通过用户ID禁用指定用户
 *
 * @param banDTO 包含被禁用用户ID的DTO对象
 * @return 返回操作结果，成功时返回ok状态
 */
    @PutMapping("/users/ban")  // 定义HTTP PUT请求映射，路径为/users/ban

    public Result banUser(@RequestBody BanDTO banDTO){  // 方法：banUser，接收BanDTO类型参数
        adminService.banUser(banDTO);  // 调用adminService的banUser方法执行禁用操作
        return Result.ok();  // 返回操作成功的结果
    }
}
