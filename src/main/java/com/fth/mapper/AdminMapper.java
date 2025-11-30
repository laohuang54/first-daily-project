package com.fth.mapper;

import com.fth.dto.BanDTO;
import com.fth.dto.LoginDTO;
import com.fth.dto.PageResult;
import com.fth.dto.UserDTO;
import com.fth.pojo.Admin;
import com.fth.pojo.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper {
    Admin login(LoginDTO loginDTO);

    List<User> getUser(UserDTO userDTO);

    List<User> getUserList(UserDTO userDTO);

    @Select("select * from user where id = #{id}")
    User getUserDetail(Long id);

    void banUser(BanDTO banDTO);

    void unbanUser(Integer id);
}
