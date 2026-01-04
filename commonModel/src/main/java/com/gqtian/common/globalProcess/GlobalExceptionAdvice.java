package com.gqtian.common.globalProcess;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.gqtian.common.enums.GlobalResultCodeEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理切面
 * <p>
 * 使用@RestControllerAdvice注解实现全局异常捕获和处理，
 * 将异常统一转换为GlobalResult返回给前端，保证API返回格式的一致性
 * </p>
 * <p>
 * 处理的异常类型：
 * <ul>
 *   <li>GlobalException - 自定义业务异常</li>
 *   <li>Exception - 所有未预期的其他异常</li>
 * </ul>
 * </p>
 *
 * @author gqtian
 * @date 2025/8/26
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

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
        log.error(e.getMessage(), e);

        // 如果某个自定义异常有@ResponseStatus注解，就继续抛出
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }

        return GlobalResult.failure(e.getMessage());
    }

    /**
     * 处理自定义业务异常
     * <p>
     * 业务异常不需要打印完整堆栈，只记录错误信息。
     * 将异常信息转换为对应的状态码和描述返回
     * </p>
     *
     * @param e 业务异常对象
     * @return 统一返回结果
     */
    @ExceptionHandler(GlobalException.class)
    public GlobalResult<?> handleGlobalException(GlobalException e) {
        // 业务异常不需要打印堆栈信息
        String message = e.getMessage();

        // 打印业务异常信息
        log.error(message, e);

        // 如果某个自定义异常有@ResponseStatus注解，就继续抛出
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
            throw e;
        }

        GlobalResultCodeEnum globalResultCodeEnum = GlobalResultCodeEnum.getResultCodeEnum(message);

        if (globalResultCodeEnum == null) {
            return GlobalResult.failure(GlobalResultCodeEnum.RC999.getCode(), message);
        }

        return GlobalResult.failure(globalResultCodeEnum.getCode(), globalResultCodeEnum.getMessage());
    }

}
