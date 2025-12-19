package com.fth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
@Configuration
public class ThreadPoolConfig {
    @Bean("cacheBuilderThreadPool")
    public ThreadPoolExecutor cacheBuilderThreadPool() {
        return new ThreadPoolExecutor(
                1,
                2, // 最大线程数
                30,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5),
                new ThreadFactory(){
                    private final AtomicInteger threadIndex = new AtomicInteger(1);//为了给线程起一个不会重复的名字
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t= new Thread(r,"cacheBuilderThreadPool-" + threadIndex.getAndIncrement());//起名字
                        t.setDaemon(false); //非守护线程
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()//当任务队列满了之后，由调用线程处理该任务
        );
    }
}

