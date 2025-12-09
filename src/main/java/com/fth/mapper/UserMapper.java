package com.fth.mapper;


import com.fth.dto.LoginDTO;
import com.fth.pojo.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper{

    @Select("select * from user where username=#{username} and password=#{password}")
    User login(LoginDTO loginDTO);

    void saveUser(User user);

    @Select("select * from user where id=#{id}")
    User getInfo(Integer id);

    void updateInfo(User user);

    @Update("update user set password = #{newPassword} where id = #{userId}")
    void updatepassword(Integer userId, String newPassword);

    @Delete("delete from user where id=#{id}")
    void deleteUser(Integer id);
    @Update("update user set score = score + #{signScore} where id = #{userId}")
    void updatescore(Integer userId, Integer signScore);
}
