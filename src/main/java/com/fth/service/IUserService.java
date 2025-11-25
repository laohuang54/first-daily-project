package com.fth.service;

import com.fth.dto.LoginDTO;
import com.fth.dto.Result;
import com.fth.pojo.User;


public interface IUserService {

    User login(LoginDTO loginDTO);

    Result register(User user);
}
