package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;


/*
全局异常捕获（全局异常处理器）
代理痛过aop save方法处理
15@ControllerAdvice
RestController.class拦截controller上加注解RestController的controller
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
@ResponseBody
public class GobalExceptionHandle {
    /*
    异常处理
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        if(ex.getMessage().contains("Duplicate entry")){
            String[] split =ex.getMessage().split(" ");
            String msg =split[2]+ "已存在";
            return R.error(msg);
        }else {
            return R.error("未知错误");
        }
    }

}
