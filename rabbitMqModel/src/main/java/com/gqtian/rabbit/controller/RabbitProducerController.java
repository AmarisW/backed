package com.gqtian.rabbit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.rabbit.message.OrderMessage;
import com.gqtian.rabbit.producer.RabbitProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 测试接口
 *
 * @author gqtian
 */
@Slf4j
@RestController
@RequestMapping("/rabbit")
@RequiredArgsConstructor
public class RabbitProducerController {

    private final RabbitProducerService producerService;

    @GetMapping("/direct")
    public String sendDirect(@RequestParam(defaultValue = "hello direct") String msg) {
        producerService.sendDirectMessage(msg);
        return "ok";
    }

    @PostMapping("/order")
    public String sendOrder(@RequestBody OrderMessage order) {
        producerService.sendOrderMessage(order);
        return "ok";
    }

    @GetMapping("/topic")
    public String sendTopic(@RequestParam String routingKey, @RequestParam(defaultValue = "topic message") String msg) {
        producerService.sendTopicMessage(routingKey, msg);
        return "ok";
    }

    @GetMapping("/fanout")
    public String sendFanout(@RequestParam(defaultValue = "fanout message") String msg) {
        producerService.sendFanoutMessage(msg);
        return "ok";
    }

    @GetMapping("/confirm")
    public String sendWithConfirm(@RequestParam String msg, @RequestParam String correlationId) {
        producerService.sendWithConfirm("direct.routing.key", msg, correlationId);
        return "ok";
    }

    @GetMapping("/persistent")
    public String sendPersistent(@RequestParam(defaultValue = "persistent message") String msg) {
        producerService.sendPersistentMessage(msg);
        return "ok";
    }

    @GetMapping("/delay")
    public String sendDelay(@RequestParam(defaultValue = "delay message") String msg,
        @RequestParam(defaultValue = "5000") String expiration) {
        producerService.sendDelayMessage(msg, expiration);
        return "ok";
    }

    @GetMapping("/batch")
    public String batchSend(@RequestParam(defaultValue = "10") int count) {
        producerService.batchSend(count);
        return "ok";
    }
}
