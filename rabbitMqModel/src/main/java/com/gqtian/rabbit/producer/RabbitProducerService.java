package com.gqtian.rabbit.producer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.gqtian.rabbit.config.RabbitMqConfig;
import com.gqtian.rabbit.message.OrderMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 生产者示例
 * <p>
 * 演示场景：直连、主题、扇出、延迟、批量、TTL、消息确认
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitProducerService {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 示例 1：发送简单字符串消息（直连）
     */
    public void sendDirectMessage(String msg) {
        log.info("发送直连消息: {}", msg);
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, RabbitMqConfig.ROUTING_KEY_DIRECT, msg);
    }

    /**
     * 示例 2：发送对象消息（直连） — JSON 序列化
     */
    public void sendOrderMessage(OrderMessage order) {
        log.info("发送订单消息: {}", order);
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, RabbitMqConfig.ROUTING_KEY_DIRECT, order);
    }

    /**
     * 示例 3：发送主题消息（通配符路由）
     *
     * @param routingKey 例如：order.created、order.paid、order.shipped
     */
    public void sendTopicMessage(String routingKey, String msg) {
        log.info("发送主题消息, routingKey={}, msg={}", routingKey, msg);
        rabbitTemplate.convertAndSend(RabbitMqConfig.TOPIC_EXCHANGE, routingKey, msg);
    }

    /**
     * 示例 4：发送扇出消息（广播到所有绑定队列）
     */
    public void sendFanoutMessage(String msg) {
        log.info("发送扇出消息: {}", msg);
        // fanout 模式 routingKey 会被忽略
        rabbitTemplate.convertAndSend(RabbitMqConfig.FANOUT_EXCHANGE, "", msg);
    }

    /**
     * 示例 5：发送带 CorrelationData 的消息（用于确认回调）
     */
    public void sendWithConfirm(String routingKey, String msg, String correlationId) {
        log.info("发送确认消息, correlationId={}, msg={}", correlationId, msg);
        CorrelationData correlationData = new CorrelationData(correlationId);
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, routingKey, msg, correlationData);
    }

    /**
     * 示例 6：发送持久化消息（重启不丢失）
     */
    public void sendPersistentMessage(String msg) {
        log.info("发送持久化消息: {}", msg);
        MessagePostProcessor processor = (Message message) -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        };
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, RabbitMqConfig.ROUTING_KEY_DIRECT, msg, processor);
    }

    /**
     * 示例 7：发送带 TTL 的消息（延迟消费）
     *
     * @param expiration 单位毫秒，例如 5000 表示 5 秒后过期
     */
    public void sendDelayMessage(String msg, String expiration) {
        log.info("发送延迟消息, expiration={}ms, msg={}", expiration, msg);
        MessagePostProcessor processor = (Message message) -> {
            message.getMessageProperties().setExpiration(expiration);
            return message;
        };
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, RabbitMqConfig.ROUTING_KEY_DIRECT, msg, processor);
    }

    /**
     * 示例 8：批量发送消息
     */
    public void batchSend(int count) {
        for (int i = 0; i < count; i++) {
            OrderMessage order = OrderMessage.builder()
                .orderId("ORDER_" + System.currentTimeMillis() + "_" + i)
                .userId(1000L + i)
                .amount(new java.math.BigDecimal("99.99"))
                .status("CREATED")
                .createTime(java.time.LocalDateTime.now())
                .build();
            sendOrderMessage(order);
        }
    }
}
