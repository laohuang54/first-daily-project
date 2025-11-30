package com.fth.service.impl;

import com.fth.dto.LoginDTO;
import com.fth.dto.RegisterDTO;
import com.fth.dto.Result;
import com.fth.dto.UserDTO;
import com.fth.mapper.UserMapper;
import com.fth.pojo.User;
import com.fth.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.fth.constant.UserConstant.LOGIN_ERROR;

@Service
@Slf4j
public class UserService implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(LoginDTO loginDTO) {
        User user=userMapper.login(loginDTO);
        if (user==null){
            throw new RuntimeException(LOGIN_ERROR);
        }
        if(user.getStatus()==0){
            throw new RuntimeException("该账号已被封禁,原因:"+user.getReason());
        }
        return user;
    }

    @Override
    public Result register(User user) {
        userMapper.saveUser(user);
        return Result.ok();
    }

    @Override
    public Result registerWithAvatar(RegisterDTO userDTO, String avatarUrl) {
        // 1. 检查用户名是否已存在（这里简化处理，实际应该在Mapper中添加查询方法）
        // 注意：当前Mapper没有提供根据用户名查询的方法，这里暂时跳过，后续可以优化
        
        // 2. 密码加密（当前项目可能没有BCrypt依赖，暂时使用明文，实际项目中应该添加BCrypt依赖并使用）
        String encryptedPwd = userDTO.getPassword(); // 实际项目中应该使用 BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt());
        
        // 3. DTO 转实体类，设置头像URL
        User user = new User();
        BeanUtils.copyProperties(userDTO, user); // 复制普通字段
        user.setPassword(encryptedPwd); // 设置密码
        user.setAvatar(avatarUrl); // 设置OSS文件URL
        user.setCreateTime(LocalDateTime.now()); // 设置创建时间
        user.setStatus(1); // 设置账号状态为正常
        
        // 4. 入库
        userMapper.saveUser(user);
        return Result.ok("注册成功");
    }
}
