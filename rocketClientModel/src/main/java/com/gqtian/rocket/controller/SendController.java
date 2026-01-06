package com.gqtian.rocket.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.rocket.rmq.ProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 模拟数据生成
 *
 * @author gqtian
 * @className RocketController
 * @date 2024/6/5 10:32
 */
@Slf4j
@RestController
@RequestMapping("/send")
@RequiredArgsConstructor
public class SendController {

    private final ProducerService producerService;

    /**
     * 发送消息
     */
    @RequestMapping("/sendMessage")
    public void sendMessage(String rocketId, String message, String topic) {
        producerService.sendMessage(rocketId, message, topic);
    }

    /**
     * 发送指定附件位置的文件
     */
    @RequestMapping("/sendByFile")
    public void sendByFile(String rocketId, String filePath, String topic, boolean isSend) {
        producerService.sendByFile(rocketId, filePath, topic, isSend);
    }

    /**
     * 发送指定附件位置的文件
     */
    @RequestMapping("/sendByFileTime")
    public void sendByFileTime(String rocketId, String filePath, String topic, Long period) {
        producerService.sendByFileTime(rocketId, filePath, topic, period);
    }
}
