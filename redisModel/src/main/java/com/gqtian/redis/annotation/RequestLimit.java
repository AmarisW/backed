package com.gqtian.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 🤨 请求限制注解
 * @className RequestLimit
 * @author gqtian
 * @date 2025/12/18 11:06
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
@Target(ElementType.METHOD) // 方法级别生效
public @interface RequestLimit {
    /**
     * 调用方唯一key的名字
     *
     * @return
     */
    String name();

    /**
     * 限制访问次数
     * @return
     */
    int limitTimes();

    /**
     * 限制时长，也就是计数器的过期时间
     *
     * @return
     */
    long timeout();

    /**
     * 限制时长单位
     *
     * @return
     */
    TimeUnit timeUnit();
}
