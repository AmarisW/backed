package com.gqtian.contrast.demo.事件监听;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.contrast.demo.事件监听.disruptor.DisruptorPublisher;
import com.gqtian.contrast.demo.事件监听.eventListener.event.MessageObjectList;
import com.gqtian.contrast.demo.事件监听.eventListener.event.MessageOne;
import com.gqtian.contrast.demo.事件监听.eventListener.event.MessageThree;
import com.gqtian.contrast.demo.事件监听.eventListener.event.MessageTwo;
import com.gqtian.contrast.demo.事件监听.eventListener.util.ContextUtil;

import lombok.RequiredArgsConstructor;

/**
 * 👻 时间监听测试
 * 
 * @className ListenerController
 * @author gqtian
 * @date 2025/2/27 16:11
 */
@RestController()
@RequestMapping("/listener")
@RequiredArgsConstructor
public class ListenerController {

    @Autowired
    private DisruptorPublisher disruptorPublisher;

    /**
     * 测试 - Spring 原生事件监听方式
     */
    @RequestMapping("/doSome")
    public void doSome(int i) {
        if (i == 1) {
            MessageOne messageOne = new MessageOne();
            messageOne.setMessage("消息一");
            ContextUtil.publishEvent(messageOne);
        } else if (i == 2) {
            MessageTwo messageTwo = new MessageTwo();
            messageTwo.setMessage("消息二");
            ContextUtil.publishEvent(messageTwo);
        } else if (i == 3) {
            MessageThree messageThree = new MessageThree();
            messageThree.setMessage("消息三");
            ContextUtil.publishEvent(messageThree);
        } else if (i == 4) {
            MessageThree messageThree = new MessageThree();
            messageThree.setMessage("消息集合1");
            ContextUtil.publishEvent(MessageObjectList.builder().message("消息集合")
                .messageList(Collections.singletonList(messageThree)).build());
        }
    }

}
