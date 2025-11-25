package com.fth.handler;

import com.fth.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result exceptionHandler(Exception ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.fail(ex.getMessage());
    }
}
