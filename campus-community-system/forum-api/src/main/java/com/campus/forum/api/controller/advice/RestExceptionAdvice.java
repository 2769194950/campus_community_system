package com.campus.forum.api.controller.advice;

import com.campus.forum.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * REST API 异常处理器
 */
@RestControllerAdvice
public class RestExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public Result handleException(Exception e) {
        logger.error("服务器发生异常: " + e.getMessage(), e);
        return Result.error(500, "服务器异常，请稍后再试!");
    }

}
