package com.gqtian.common.globalProcess;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.gqtian.common.enums.ResultCodeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 *
 * @author gqtian
 * @date 2023/04/26 10:49
 */
@Slf4j
@Order(9999999)
@RestControllerAdvice
public class GlobalExceptionAdvice {
    
    /**
     * 处理所有不可知的异常
     *
     * @param e 异常
     * @return Result
     * @throws Exception 异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e)
        throws Exception {
        log.error(e.getMessage(), e);
        
        // 如果某个自定义异常有@ResponseStatus注解，就继续抛出
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }
        
        return Result.failure(e.getMessage());
    }
    
    /**
     * 处理所有业务异常
     *
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        // 业务异常不需要打印堆栈信息
        String message = e.getMessage();
        
        // 打印业务异常信息
        log.error(message, e);
        
        // 如果某个自定义异常有@ResponseStatus注解，就继续抛出
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }
        
        ResultCodeEnum resultCodeEnum = ResultCodeEnum.getResultCodeEnum(message);
        
        if (resultCodeEnum == null) {
            return Result.failure(ResultCodeEnum.RC999.getCode(), message);
        }
        
        return Result.failure(resultCodeEnum.getCode(), resultCodeEnum.getMessage());
    }
    
}
