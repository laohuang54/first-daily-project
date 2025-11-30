package com.fth.controller.user;

import com.fth.dto.LoginDTO;
import com.fth.dto.RegisterDTO;
import com.fth.dto.Result;
import com.fth.dto.UserDTO;
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
import org.springframework.web.bind.annotation.ModelAttribute;

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
    public Result register(@ModelAttribute RegisterDTO userDTO) {
        log.info("用户注册：{}", userDTO);
        try {
            String avatarUrl = null;
            // 1. 获取前端传递的头像文件（从DTO中取出MultipartFile）
            MultipartFile avatarFile = userDTO.getAvatar();

            // 2. 处理文件上传（有头像且文件有效才上传）
            if (avatarFile != null && !avatarFile.isEmpty()) {
                // 2.1 提取文件后缀（保持你的原有逻辑）
                String originalFilename = avatarFile.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                // 2.2 构造OSS中的唯一文件名（避免重名，保持原有逻辑）
                String objectName = "user-avatar/" + UUID.randomUUID().toString() + extension;

                // 2.3 调用改造后的AliOssUtil：传递 MultipartFile 和 objectName（核心修改）
                avatarUrl = aliOssUtil.upload(avatarFile.getBytes(), objectName);

                // 2.4 修复日志：打印实际的上传URL（原代码漏传参数）
                log.info("文件上传成功，URL：{}", avatarUrl);
            }

            // 3. 传递DTO和头像URL给Service层（逻辑不变）
            return userService.registerWithAvatar(userDTO, avatarUrl);

        } catch (IllegalArgumentException e) {
            // 捕获工具类中的参数校验错误（如文件为空、格式不支持、配置缺失）
            log.error("注册失败：{}", e.getMessage());
            return Result.fail(e.getMessage());
        } catch (com.aliyun.oss.OSSException e) {
            // 捕获OSS服务端错误（如AK错误、Bucket不存在、权限不足）
            log.error("OSS上传失败：错误码={}, 错误信息={}", e.getErrorCode(), e.getErrorMessage());
            return Result.fail(UPLOAD_ERROR + "：OSS服务异常");
        } catch (com.aliyun.oss.ClientException e) {
            // 捕获OSS客户端错误（如网络不通、Endpoint错误）
            log.error("OSS上传失败：客户端错误={}", e.getMessage());
            return Result.fail(UPLOAD_ERROR + "：网络异常，请重试");
        } catch (Exception e) {
            // 捕获其他未知错误（如文件转字节流失败）
            log.error("注册失败：", e);
            return Result.fail("注册失败，请联系管理员");
        }
    }
}
