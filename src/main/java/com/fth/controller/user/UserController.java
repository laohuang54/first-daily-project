package com.fth.controller.user;

import com.fth.dto.LoginDTO;
import com.fth.dto.Result;
import com.fth.pojo.User;
import com.fth.properties.JwtProperty;
import com.fth.service.IUserService;
import com.fth.service.impl.UserService;
import com.fth.utils.AliOssUtil;
import com.fth.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.fth.constant.JwtConstant.USER_ID;
import static com.fth.constant.UserConstant.UPLOAD_ERROR;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private JwtProperty jwtProperty;
    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        log.info("用户登录：{}",loginDTO);
        User login = userService.login(loginDTO);
        Map<String,Object> claims =new HashMap<>();
        claims.put(USER_ID,login.getId());
        String jwt = JwtUtil.createJWT(jwtProperty.getUserSecretKey()
                , jwtProperty.getUserTtl()
                , claims);
        login.setToken(jwt);
        return Result.ok(login);
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/upload")
    public Result upload(MultipartFile file){
        log.info("文件上传：{}",file);
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀   dfdfdf.png
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;

            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.ok(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }

        return Result.fail(UPLOAD_ERROR);
    }


}
