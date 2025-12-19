package com.fth.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisData { //用来实现逻辑过期
    private long expireTime;

    private Object data;
}
