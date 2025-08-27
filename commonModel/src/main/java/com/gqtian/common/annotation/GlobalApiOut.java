package com.gqtian.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 打印接口信息注解
 * <p>
 * 用于标记和描述API接口方法的信息
 * </p>
 *
 * @author gqtian
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GlobalApiOut {

    /**
     * API的注释信息
     * 
     * @return 注释内容
     */
    String value() default "";

    /**
     * API的请求参数说明
     * 
     * @return 参数说明
     */
    String params() default "";

}