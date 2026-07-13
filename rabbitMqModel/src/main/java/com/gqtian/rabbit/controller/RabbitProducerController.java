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

    /**
     * 发送直连字符串消息
     *
     * @param msg 消息内容
     */
    @GetMapping("/direct")
    public String sendDirect(@RequestParam(defaultValue = "hello direct") String msg) {
        producerService.sendDirectMessage(msg);
        return "ok";
    }

    /**
     * 发送直连对象消息（JSON 序列化）
     *
     * @param order 订单消息体
     */
    @PostMapping("/order")
    public String sendOrder(@RequestBody OrderMessage order) {
        producerService.sendOrderMessage(order);
        return "ok";
    }

    /**
     * 发送主题消息（通配符路由）
     *
     * @param routingKey 路由键，如 order.created、order.paid
     * @param msg        消息内容
     */
    @GetMapping("/topic")
    public String sendTopic(@RequestParam String routingKey, @RequestParam(defaultValue = "topic message") String msg) {
        producerService.sendTopicMessage(routingKey, msg);
        return "ok";
    }

    /**
     * 发送扇出消息（广播到所有绑定队列）
     *
     * @param msg 消息内容
     */
    @GetMapping("/fanout")
    public String sendFanout(@RequestParam(defaultValue = "fanout message") String msg) {
        producerService.sendFanoutMessage(msg);
        return "ok";
    }

    /**
     * 发送带确认回调的消息
     *
     * @param routingKey    路由键
     * @param msg           消息内容
     * @param correlationId 关联 ID，用于确认回调匹配
     */
    @GetMapping("/confirm")
    public String sendWithConfirm(@RequestParam(defaultValue = "direct.routing.key.object") String routingKey,
        @RequestParam String msg, @RequestParam String correlationId) {
        producerService.sendWithConfirm(routingKey, msg, correlationId);
        return "ok";
    }

    /**
     * 发送持久化消息（队列 + 消息双重持久化）
     *
     * @param msg 消息内容
     */
    @GetMapping("/persistent")
    public String sendPersistent(@RequestParam(defaultValue = "persistent message") String msg) {
        producerService.sendPersistentMessage(msg);
        return "ok";
    }

    /**
     * 发送带 TTL 的延迟消息
     *
     * @param msg       消息内容
     * @param expiration 过期时间（毫秒）
     */
    @GetMapping("/delay")
    public String sendDelay(@RequestParam(defaultValue = "delay message") String msg,
        @RequestParam(defaultValue = "5000") long expiration) {
        producerService.sendDelayMessage(msg, expiration);
        return "ok";
    }

    /**
     * 批量发送订单消息
     *
     * @param count 发送数量
     */
    @GetMapping("/batch")
    public String batchSend(@RequestParam(defaultValue = "10") int count) {
        producerService.batchSend(count);
        return "ok";
    }
}
