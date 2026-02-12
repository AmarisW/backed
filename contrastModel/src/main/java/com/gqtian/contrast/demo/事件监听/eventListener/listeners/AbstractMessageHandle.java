package com.gqtian.contrast.demo.事件监听.eventListener.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.gqtian.contrast.demo.事件监听.eventListener.event.AbstractMessage;

public abstract class AbstractMessageHandle<T extends AbstractMessage> {

    @Async
    @EventListener
    public abstract void doAction(T t);
}
