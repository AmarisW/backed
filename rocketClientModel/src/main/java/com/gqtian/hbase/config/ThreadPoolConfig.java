package com.gqtian.hbase.config;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 👻 线程池配置
 *
 * @className ThreadPoolConfig
 * @author gqtian
 * @date 2024/7/1 16:43
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "testThread")
    public ThreadPoolExecutor getTest() {
        return new ThreadPoolExecutor(10, 20, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            new BasicThreadFactory.Builder().namingPattern("查询线程-%d").build(), new ThreadPoolExecutor.AbortPolicy());
    }

}