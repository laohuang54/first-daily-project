package com.fth.service.impl;

import com.fth.dto.LoginDTO;
import com.fth.dto.Result;
import com.fth.mapper.UserMapper;
import com.fth.pojo.User;
import com.fth.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
