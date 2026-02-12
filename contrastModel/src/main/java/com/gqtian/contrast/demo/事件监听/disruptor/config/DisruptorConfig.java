package com.gqtian.contrast.demo.事件监听.disruptor.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 Disruptor 配置类
 * 
 * @className DisruptorConfig
 * @author gqtian
 * @date 2026/2/3
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DisruptorConfig {

    /**
     * Disruptor 实例
     */
    private Disruptor<DisruptorAbstractMessageEvent> disruptor;

    /**
     * 创建 Disruptor 实例
     */
    @Bean
    public Disruptor<DisruptorAbstractMessageEvent> messageDisruptor() {
        // RingBuffer 大小，必须是 2 的 N 次方
        int bufferSize = 1024;

        // 定义用于事件处理的线程池， Disruptor通过 java.util.concurrent.ExecutorSerivce 提供的线程来触发consumer的事件处理
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 创建 Disruptor
        disruptor = new Disruptor<>(new DisruptorEventFactory(), bufferSize, executor, ProducerType.SINGLE, // 单生产者模式
            new BlockingWaitStrategy() // 阻塞等待策略
        );

        // 设置事件处理器
        disruptor.handleEventsWith((event, sequence, endOfBatch) -> dispatchToHandlers(event));

        // 启动 Disruptor
        disruptor.start();

        log.info("Disruptor 初始化完成，RingBuffer 大小: {}", bufferSize);

        return disruptor;
    }

    /**
     * 创建 RingBuffer Bean
     */
    @Bean
    public RingBuffer<DisruptorAbstractMessageEvent>
        messageRingBuffer(Disruptor<DisruptorAbstractMessageEvent> disruptor) {
        return disruptor.getRingBuffer();
    }

    /**
     * 销毁 Disruptor
     */
    @PreDestroy
    public void destroy() {
        if (disruptor != null) {
            log.info("关闭 Disruptor");
            disruptor.shutdown();
        }
    }

    /**
     * 分派事件给相应的处理器
     */
    private void dispatchToHandlers(DisruptorAbstractMessageEvent event) {

    }

}
