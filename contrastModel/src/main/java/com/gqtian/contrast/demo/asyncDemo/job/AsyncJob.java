package com.gqtian.contrast.demo.asyncDemo.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gqtian.contrast.demo.asyncDemo.service.AsyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 异步线程实现类
 * @className AsynService
 * @author gqtian
 * @date 2025/11/19 10:09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncJob {
    private final AsyncService asyncService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void async() {
        asyncService.asyncMethod();
    }
}
