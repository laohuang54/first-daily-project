package com.fth.service.impl;


import com.fth.dto.BanDTO;
import com.fth.dto.LoginDTO;
import com.fth.dto.PageResult;
import com.fth.dto.UserDTO;
import com.fth.mapper.AdminMapper;
import com.fth.pojo.Admin;
import com.fth.pojo.User;
import com.fth.service.IAdminService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
    public List<User> getUser(UserDTO userDTO) {
        // 直接调用Mapper查询所有符合条件的用户，不进行分页

        List<User> user = adminMapper.getUser(userDTO);
        log.info("用户数据列表"+user);
        return user;
    }

    @Override
    public User getUserDetail(Long id) {
        log.info("单个用户查看");
        return adminMapper.getUserDetail(id);
    }

    @Override
    public void banUser(BanDTO banDTO) {
        log.info("用户封禁"+banDTO);
        if(banDTO.getStatus()==1)
        adminMapper.banUser(banDTO);
        else {
            adminMapper.unbanUser(banDTO.getId());
        }
    }
}
