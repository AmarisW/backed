package com.gqtian.contrast.demo.事件监听.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 👻 消息一
 * 
 * @className MessageOne
 * @author gqtian
 * @date 2025/2/27 16:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageTwo extends AbstractMessage {
    private String message;

}
