package com.gqtian.contrast.demo.事件监听.eventListener.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 👻 消息一
 * 
 * @className DisruptorMessageOne
 * @author gqtian
 * @date 2025/2/27 16:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageOne extends AbstractMessage {
    private String message;

}
