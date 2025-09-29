package com.gqtian.contrast.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 👻 线程池配置
 *
 * @className ThreadPoolConfig
 * @author gqtian
 * @date 2024/7/1 16:43
 */
@Configuration
public class ThreadPoolConfig {

    /** 查询处理线程池 */
    @Bean(name = "queryThread")
    public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        // 获取当前系统的CPU数目
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程数
        taskExecutor.setCorePoolSize(1);
        // 最大线程数
        taskExecutor.setMaxPoolSize(1);
        // 阻塞队列长度
        taskExecutor.setQueueCapacity(1);
        // 空闲线程最大存活时间
        // taskExecutor.setKeepAliveSeconds(100);
        // 指定用于新创建的线程名称的前缀
        taskExecutor.setThreadNamePrefix("query-threadPool-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

    /** 查询处理线程池 */
    @Bean(name = "testThread")
    public ThreadPoolTaskExecutor getTest() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("查询线程-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }
}