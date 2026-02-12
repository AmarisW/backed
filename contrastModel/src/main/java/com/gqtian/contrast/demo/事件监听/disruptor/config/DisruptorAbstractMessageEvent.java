package com.gqtian.contrast.demo.事件监听.disruptor.config;

import com.gqtian.contrast.demo.事件监听.disruptor.event.DisruptorAbstractMessage;

import lombok.Data;

/**
 * 👻 Disruptor 事件包装类
 * 用于在 RingBuffer 中传递消息
 * 
 * @className DisruptorAbstractMessageEvent
 * @author gqtian
 * @date 2026/2/3
 */
@Data
public class DisruptorAbstractMessageEvent {

    /**
     * 实际的消息对象
     */
    private DisruptorAbstractMessage data;

    /**
     * 清空事件数据（重用对象）
     */
    public void clear() {
        this.data = null;
    }
}
