package com.gqtian.contrast.demo.eventListenerDemo.listeners;

import org.springframework.stereotype.Service;

import com.gqtian.contrast.demo.eventListenerDemo.event.MessageTwo;

import lombok.extern.slf4j.Slf4j;

/**
 * 👻 消息一
 * 
 * @className MessageOne
 * @author gqtian
 * @date 2025/2/27 16:08
 */
@Slf4j
@Service
public class MessageTwoHandle extends AbstractMessageHandle<MessageTwo> {

    @Override
    public void doAction(MessageTwo messageTwo) {
        log.info("消息二：{}", messageTwo.getMessage());
    }
}
