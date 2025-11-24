package com.gqtian.hbase.config;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * 👻 线程池配置
 * 
 * @className ThreadPoolConfig
 * @author gqtian
 * @date 2024/7/1 16:43
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    @Value("${hbase.thread-size}")
    private int dealThreadPoolSize;

    /**
     * 查询处理线程池
     */
    @Bean(name = "dealThread")
    public ThreadPoolExecutor get() {

        if (dealThreadPoolSize == 0) {
            log.info("dealThreadPoolSize is 0,please check your config");
        } else {
            dealThreadPoolSize = 5;
        }
        return new ThreadPoolExecutor(dealThreadPoolSize, dealThreadPoolSize * 2, 0, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new BasicThreadFactory.Builder().namingPattern("deal-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());
    }
}
