package com.gqtian.contrast.demo.asyncDemo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 异步线程实现类
 * @className AsynService
 * @author gqtian
 * @date 2025/11/19 10:09
 */
@Slf4j
@Service
public class AsyncService {

    @Async("asyncThread")
    public void asyncMethod() {
        log.info("异步线程开始执行 {}", DateTime.now());
    }
}
