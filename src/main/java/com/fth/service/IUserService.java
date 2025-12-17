package com.fth.service;

import com.fth.dto.LoginDTO;
import com.fth.dto.RegisterDTO;
import com.fth.dto.Result;
import com.fth.pojo.User;
import com.fth.vo.UserVO;


public interface IUserService {

    User login(LoginDTO loginDTO);

    Result register(User user);

    Result registerWithAvatar(RegisterDTO userDTO, String avatarUrl);

    User getUserInfo(Integer id);

    UserVO getOthers(Integer id);

    Result updateInfo(User user);

    Result updatePassword(String password, String newPassword);

    Result deleteUser(Integer id);

    Result getCategoryInfo();
}
