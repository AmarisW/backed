package com.gqtian.contrast.demo.事件监听.eventListener.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 👻 消息三
 * 
 * @className DisruptorMessageThree
 * @author gqtian
 * @date 2025/2/27 16:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageThree extends AbstractMessage {
    private String message;
}
