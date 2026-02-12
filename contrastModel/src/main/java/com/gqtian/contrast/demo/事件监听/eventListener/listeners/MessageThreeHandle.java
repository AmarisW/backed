package com.gqtian.contrast.demo.事件监听.eventListener.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.gqtian.contrast.demo.事件监听.eventListener.event.MessageObjectList;
import com.gqtian.contrast.demo.事件监听.eventListener.event.MessageThree;

import lombok.extern.slf4j.Slf4j;

/**
 * 👻 消息一
 * 
 * @className DisruptorMessageOne
 * @author gqtian
 * @date 2025/2/27 16:08
 */
@Slf4j
@Service
public class MessageThreeHandle {
    @Async
    @EventListener
    public void doAction(MessageObjectList messageObjectList) {
        log.info("消息四：{}", JSON.toJSON(messageObjectList));
    }

    @Async
    @EventListener
    public void doAction(MessageThree messageThree) {
        log.info("消息三：{}", messageThree.getMessage());
    }

}
