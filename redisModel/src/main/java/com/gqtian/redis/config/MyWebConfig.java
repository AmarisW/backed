package com.gqtian.redis.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gqtian.redis.interceptor.FixedWindowLimitInterceptor;
import com.gqtian.redis.interceptor.SlidingWindowLimitInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 WebMvcConfigurer拦截器
 * @className MyWebConfig
 * @author gqtian
 * @date 2025/12/18 11:13
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MyWebConfig implements WebMvcConfigurer {

    /**
     * 固定窗口限流拦截器
     */
    private final FixedWindowLimitInterceptor fixedWindowLimitInterceptor;

    /**
     * 滑动窗口限流拦截器
     */
    private final SlidingWindowLimitInterceptor slidingWindowLimitInterceptor;

    /**
     * 添加拦截器
     *
     * @param registry 拦截器注册
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 添加接口限流拦截器
        // registry.addInterceptor(fixedWindowLimitInterceptor) // 添加拦截器
        // .addPathPatterns("/**") // 添加拦截路径 所有请求
        // .order(1); // 设置优先级

        // // 添加滑动窗口限流拦截器
        // registry.addInterceptor(slidingWindowLimitInterceptor) // 添加拦截器
        // .addPathPatterns("/**") // 添加拦截路径 所有请求
        // .order(2); // 设置优先级
        // WebMvcConfigurer.super.addInterceptors(registry);
    }
}
