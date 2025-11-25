package com.fth.service.impl;

import com.fth.service.ISignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SignService implements ISignService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
}
