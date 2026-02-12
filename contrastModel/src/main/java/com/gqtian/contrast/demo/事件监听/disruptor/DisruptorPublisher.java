package com.gqtian.contrast.demo.事件监听.disruptor;

import org.springframework.stereotype.Component;

import com.gqtian.contrast.demo.事件监听.disruptor.config.DisruptorAbstractMessageEvent;
import com.gqtian.contrast.demo.事件监听.disruptor.event.DisruptorAbstractMessage;
import com.lmax.disruptor.RingBuffer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 Disruptor 消息发布器
 * 
 * @className DisruptorPublisher
 * @author gqtian
 * @date 2026/2/3
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DisruptorPublisher {

    private final RingBuffer<DisruptorAbstractMessageEvent> ringBuffer;

    /**
     * 发布消息
     *
     * @param message 消息对象
     */
    public void publishEvent(DisruptorAbstractMessage message) {
        if (message == null) {
            log.warn("发布的消息为空");
            return;
        }

        // 获取 RingBuffer 中的下一个序号
        long sequence = ringBuffer.next();
        try {
            // 获取该序号对应的事件对象
            DisruptorAbstractMessageEvent event = ringBuffer.get(sequence);
            // 设置消息数据
            event.setData(message);

            log.debug("发布消息: type={}, sequence={}", message.getClass().getSimpleName(), sequence);
        } finally {
            // 发布事件
            ringBuffer.publish(sequence);
        }
    }

    /**
     * 批量发布消息
     *
     * @param messages 消息列表
     */
    public void publishEvents(DisruptorAbstractMessage... messages) {
        if (messages == null || messages.length == 0) {
            return;
        }

        for (DisruptorAbstractMessage message : messages) {
            publishEvent(message);
        }
    }
}
