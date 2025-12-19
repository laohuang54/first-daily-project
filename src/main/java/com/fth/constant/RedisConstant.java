package com.fth.constant;

import java.util.Random;

public class RedisConstant {
    public static final long EXPIRE_TIME= new Random().nextInt(10*60*1000)+30*60*1000; //随机过期时间 30~40分钟

}
