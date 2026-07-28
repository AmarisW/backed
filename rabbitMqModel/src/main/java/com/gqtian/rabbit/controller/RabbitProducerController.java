package com.gqtian.rabbit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.rabbit.config.RabbitMqConfig;
import com.gqtian.rabbit.message.OrderMessage;
import com.gqtian.rabbit.producer.RabbitProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 生产者 HTTP 调试接口。
 * <p>统一前缀 {@code /rabbit}，便于本地联调各交换机模式。</p>
 *
 * @author gqtian
 * @see RabbitProducerService
 */
@Slf4j
@RestController
@RequestMapping("/rabbit")
@RequiredArgsConstructor
public class RabbitProducerController {

    private final RabbitProducerService producerService;

    /**
     * 发送直连字符串消息。
     *
     * @param msg 消息内容
     * @return 固定 {@code ok}
     */
    @GetMapping("/direct")
    public String sendDirect(@RequestParam(defaultValue = "hello direct") String msg) {
        producerService.sendDirectMessage(msg);
        return "ok";
    }

    /**
     * 发送直连订单对象（JSON 请求体）。
     *
     * @param order 订单消息体
     * @return 固定 {@code ok}
     */
    @PostMapping("/order")
    public String sendOrder(@RequestBody OrderMessage order) {
        producerService.sendOrderMessage(order);
        return "ok";
    }

    /**
     * 发送主题消息。
     *
     * @param routingKey 路由键，如 order.created、order.paid
     * @param msg        消息内容
     * @return 固定 {@code ok}
     */
    @GetMapping("/topic")
    public String sendTopic(@RequestParam String routingKey,
        @RequestParam(defaultValue = "topic message") String msg) {
        producerService.sendTopicMessage(routingKey, msg);
        return "ok";
    }

    /**
     * 发送扇出广播消息。
     *
     * @param msg 消息内容
     * @return 固定 {@code ok}
     */
    @GetMapping("/fanout")
    public String sendFanout(@RequestParam(defaultValue = "fanout message") String msg) {
        producerService.sendFanoutMessage(msg);
        return "ok";
    }

    /**
     * 发送带发布确认的字符串消息。
     * <p>
     * 默认走字符串队列路由键；若改用 object 路由键，消息体也必须是 OrderMessage，
     * 否则消费者反序列化失败。
     * </p>
     *
     * @param routingKey    路由键，默认 {@link RabbitMqConfig#ROUTING_KEY_DIRECT_STRING}
     * @param msg           消息内容
     * @param correlationId 关联 ID，与 ConfirmCallback 对应
     * @return 固定 {@code ok}
     */
    @GetMapping("/confirm")
    public String sendWithConfirm(
        @RequestParam(defaultValue = RabbitMqConfig.ROUTING_KEY_DIRECT_STRING) String routingKey,
        @RequestParam String msg, @RequestParam String correlationId) {
        producerService.sendWithConfirm(routingKey, msg, correlationId);
        return "ok";
    }

    /**
     * 显式发送持久化消息（教学接口；convertAndSend 默认已是 PERSISTENT）。
     *
     * @param msg 消息内容
     * @return 固定 {@code ok}
     */
    @GetMapping("/persistent")
    public String sendPersistent(@RequestParam(defaultValue = "persistent message") String msg) {
        producerService.sendPersistentMessage(msg);
        return "ok";
    }

    /**
     * 发送延迟消息（TTL + 延迟队列 + 死信转发到字符串业务队列）。
     *
     * @param msg        消息内容
     * @param expiration 延迟毫秒数，到期后进入直连字符串队列
     * @return 固定 {@code ok}
     */
    @GetMapping("/delay")
    public String sendDelay(@RequestParam(defaultValue = "delay message") String msg,
        @RequestParam(defaultValue = "5000") long expiration) {
        producerService.sendDelayMessage(msg, expiration);
        return "ok";
    }

    /**
     * 批量发送订单消息。
     *
     * @param count 条数
     * @return 固定 {@code ok}
     */
    @GetMapping("/batch")
    public String batchSend(@RequestParam(defaultValue = "10") int count) {
        producerService.batchSend(count);
        return "ok";
    }
}
