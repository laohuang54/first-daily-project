package com.fth;

import cn.hutool.core.bean.BeanUtil;
import com.fth.dto.UserDTO;
import com.fth.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@SpringBootTest
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test(){
        stringRedisTemplate.opsForValue().set("name","fth");
    }
    public void test01(){
        User user=new User();
        user.setId(1);
        user.setUsername("fth");
        user.setPassword("123456");
        UserDTO userDTO= BeanUtil.copyProperties(user,UserDTO.class);
    }
}
