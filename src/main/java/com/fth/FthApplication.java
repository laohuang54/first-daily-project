package com.fth;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.fth.mapper")
@Slf4j
public class FthApplication {

    public static void main(String[] args) {
        log.info("Spring 开始启动!!!");
        SpringApplication.run(FthApplication.class, args);
        log.info("Spring 启动成功!!!");
    }
}
