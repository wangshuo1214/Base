package com.ws.base.exception;

import com.ws.base.constant.HttpStatus;
import com.ws.base.entity.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ControllerAdvice注解对异常进行统一的处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = BaseException.class)
    public Result handlerException(BaseException e){
        return Result.returnCodeMessage(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(value = NullPointerException.class)
    public Result handlerException(NullPointerException e){
        logger.error("发生空指针异常！原因是:",e);
        return Result.returnCodeMessage(HttpStatus.BAD_REQUEST,e.getMessage());
    }
}

