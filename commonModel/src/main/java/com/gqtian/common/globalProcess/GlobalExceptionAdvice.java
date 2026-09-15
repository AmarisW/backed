package com.gqtian.common.globalProcess;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.gqtian.common.enums.GlobalResultCodeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理切面
 * <p>
 * 使用@RestControllerAdvice注解实现全局异常捕获和处理，
 * 将异常统一转换为GlobalResult返回给前端，保证API返回格式的一致性
 * </p>
 *
 * @author gqtian
 * @date 2025/8/26
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 处理自定义业务异常
     *
     * @param e 业务异常对象
     * @return 统一返回结果
     */
    @ExceptionHandler(GlobalException.class)
    public GlobalResult<?> handleGlobalException(GlobalException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage(), e);

        // 如果某个自定义异常有@ResponseStatus注解，就继续抛出
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }

        return GlobalResult.failure(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid 校验失败）
     *
     * @param e 异常对象
     * @return 统一返回结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public GlobalResult<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验失败: {}", message);
        return GlobalResult.failure(GlobalResultCodeEnum.RC999.getCode(), message);
    }

    /**
     * 处理绑定异常
     *
     * @param e 异常对象
     * @return 统一返回结果
     */
    @ExceptionHandler(BindException.class)
    public GlobalResult<?> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.warn("参数绑定失败: {}", message);
        return GlobalResult.failure(GlobalResultCodeEnum.RC999.getCode(), message);
    }

    /**
     * 处理缺少请求参数异常
     *
     * @param e 异常对象
     * @return 统一返回结果
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public GlobalResult<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = "缺少必要参数: " + e.getParameterName();
        log.warn(message);
        return GlobalResult.failure(GlobalResultCodeEnum.RC999.getCode(), message);
    }

    /**
     * 处理参数类型不匹配异常
     *
     * @param e 异常对象
     * @return 统一返回结果
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public GlobalResult<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = "参数类型错误: " + e.getName();
        log.warn(message);
        return GlobalResult.failure(GlobalResultCodeEnum.RC999.getCode(), message);
    }

    /**
     * 处理请求体无法读取异常
     *
     * @param e 异常对象
     * @return 统一返回结果
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public GlobalResult<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return GlobalResult.failure(GlobalResultCodeEnum.RC999.getCode(), "请求参数格式错误");
    }

    /**
     * 处理请求方法不支持异常
     *
     * @param e 异常对象
     * @return 统一返回结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public GlobalResult<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String message = "不支持的请求方法: " + e.getMethod();
        log.warn(message);
        return GlobalResult.failure(GlobalResultCodeEnum.RC999.getCode(), message);
    }

    /**
     * 处理所有未预期的异常
     * <p>
     * 当异常有@ResponseStatus注解时，继续抛出由Spring处理；
     * 否则封装为GlobalResult返回
     * </p>
     *
     * @param e 异常对象
     * @return 统一返回结果
     * @throws Exception 带有@ResponseStatus注解的异常会继续抛出
     */
    @ExceptionHandler(Exception.class)
    public GlobalResult<?> handleException(Exception e) throws Exception {
        log.error("系统异常: {}", e.getMessage(), e);

        // 如果某个自定义异常有@ResponseStatus注解，就继续抛出
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }

        return GlobalResult.failure(GlobalResultCodeEnum.RC500.getCode(), GlobalResultCodeEnum.RC500.getMessage());
    }
}
