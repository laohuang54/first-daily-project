package com.fth.service;

import com.fth.dto.LoginDTO;
import com.fth.dto.RegisterDTO;
import com.fth.dto.Result;
import com.fth.dto.UserDTO;
import com.fth.pojo.User;


public interface IUserService {

    User login(LoginDTO loginDTO);

    Result register(User user);

    Result registerWithAvatar(RegisterDTO userDTO, String avatarUrl);
}
