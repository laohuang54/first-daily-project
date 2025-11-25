package com.fth.mapper;


import com.fth.dto.LoginDTO;
import com.fth.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper{

    @Select("select * from user where username=#{username} and password=#{password}")
    User login(LoginDTO loginDTO);

    void saveUser(User user);
}
