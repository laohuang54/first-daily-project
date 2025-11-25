package com.fth.service.impl;


import com.fth.dto.BanDTO;
import com.fth.dto.LoginDTO;
import com.fth.dto.UserDTO;
import com.fth.mapper.AdminMapper;
import com.fth.pojo.Admin;
import com.fth.pojo.User;
import com.fth.service.IAdminService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService implements IAdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin login(LoginDTO loginDTO) {
        // 使用 LambdaQueryWrapper 避免硬编码字段名
        Admin admin=adminMapper.login(loginDTO);
        if (admin == null) {
            throw new RuntimeException("账号或密码错误");
        }
        return admin;
    }

    @Override
    public PageInfo<User> getUser(UserDTO userDTO) {
        PageHelper.startPage(userDTO.getPage(), userDTO.getSize());

        List<User> user = adminMapper.getUser(userDTO);

        return new PageInfo<>(user);
    }

    @Override
    public User getUserDetail(Long id) {

        return adminMapper.getUserDetail(id);
    }

    @Override
    public void banUser(BanDTO banDTO) {
        if(banDTO.getStatus()==1)
        adminMapper.banUser(banDTO);
        else {
            adminMapper.unbanUser(banDTO.getId());
        }
    }
}
