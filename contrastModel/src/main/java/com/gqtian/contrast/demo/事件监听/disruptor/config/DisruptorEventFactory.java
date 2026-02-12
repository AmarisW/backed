package com.gqtian.contrast.demo.事件监听.disruptor.config;

import com.lmax.disruptor.EventFactory;

/**
 * 👻 消息事件工厂
 * 用于创建 DisruptorAbstractMessageEvent 实例
 * 
 * @className DisruptorEventFactory
 * @author gqtian
 * @date 2026/2/3
 */
public class DisruptorEventFactory implements EventFactory<DisruptorAbstractMessageEvent> {

    @Override
    public DisruptorAbstractMessageEvent newInstance() {
        return new DisruptorAbstractMessageEvent();
    }
}
