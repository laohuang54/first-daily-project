package com.fth.handler;

import com.fth.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        // 关键：打印完整栈轨迹，定位报错的类/方法/行号
        e.printStackTrace(); // 控制台打印
        log.error("异常信息：", e); // 日志文件打印（带栈）
        return Result.fail("系统异常");
    }
//    @ExceptionHandler
//    public Result exceptionHandler(Exception ex){
//        log.error("异常信息：{}", ex.getMessage());
//        return Result.fail(ex.getMessage());
//    }
}
