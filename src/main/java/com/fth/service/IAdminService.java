package com.fth.service;

import com.fth.dto.*;
import com.fth.pojo.Admin;
import com.fth.pojo.User;

import java.util.List;

public interface IAdminService {

    Admin login(LoginDTO loginDTO);

/**
 * 根据用户数据传输对象(UserDTO)获取用户列表
 *
 * @param userDTO 用户数据传输对象，包含查询用户所需的信息
 * @return 返回一个User类型的列表，包含符合条件的所有用户信息
 */
    List<User> getUser(UserDTO userDTO);

    User getUserDetail(Long id);

    void banUser(BanDTO banDTO);
}
