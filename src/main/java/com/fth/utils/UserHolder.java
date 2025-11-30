package com.fth.utils;

import com.fth.pojo.User;

public class UserHolder {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    public  static Integer getUser(){
        return Math.toIntExact(tl.get());
    }
    public static void saveUser(Long userId){
        tl.set(userId);
    }

    public  static void removeUser(){
        tl.remove();
    }
}
