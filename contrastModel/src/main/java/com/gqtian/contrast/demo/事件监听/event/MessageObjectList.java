package com.gqtian.contrast.demo.事件监听.event;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 👻 消息集合
 * 
 * @className MessageObjectList
 * @author gqtian
 * @date 2025/3/13 15:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageObjectList extends AbstractMessage {
    private String message;

    private List<MessageThree> messageList;
}
