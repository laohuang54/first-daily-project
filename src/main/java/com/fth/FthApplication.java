package com.fth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fth.mapper")
public class FthApplication {

    public static void main(String[] args) {
        SpringApplication.run(FthApplication.class, args);
    }

}
