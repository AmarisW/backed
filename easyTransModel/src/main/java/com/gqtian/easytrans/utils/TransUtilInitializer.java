package com.gqtian.easytrans.utils;

import org.dromara.trans.service.impl.TransService;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;

/**
 * TransUtil 静态注入器
 * <p>
 * 在 Spring 启动时将 TransService 注入到 TransUtil 静态变量中
 * </p>
 */
@Component
@RequiredArgsConstructor
public class TransUtilInitializer {

    private final TransService transService;

    @PostConstruct
    public void init() {
        TransUtil.setTransService(transService);
    }
}