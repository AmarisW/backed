package com.gqtian.rabbit.producer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
 * RabbitMQ 生产者示例服务。
 * <p>
 * 覆盖直连、主题、扇出、发布确认、持久化、延迟（TTL + 死信转发）与批量发送。
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
     * 直连发送字符串消息。
     *
     * @param msg 消息内容
     */
    public void sendDirectMessage(String msg) {
        log.info("发送直连消息: {}", msg);
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, RabbitMqConfig.ROUTING_KEY_DIRECT_STRING, msg);
    }

    /**
     * 直连发送订单对象（经 JSON 序列化）。
     *
     * @param order 订单消息体
     */
    public void sendOrderMessage(OrderMessage order) {
        log.info("发送订单消息: {}", order);
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, RabbitMqConfig.ROUTING_KEY_DIRECT_OBJECT, order);
    }

    /**
     * 主题发送：由 routingKey 决定匹配哪些绑定队列。
     *
     * @param routingKey 如 order.created、order.paid、order.shipped
     * @param msg        消息内容
     */
    public void sendTopicMessage(String routingKey, String msg) {
        log.info("发送主题消息, routingKey={}, msg={}", routingKey, msg);
        rabbitTemplate.convertAndSend(RabbitMqConfig.TOPIC_EXCHANGE, routingKey, msg);
    }

    /**
     * 扇出广播：所有绑定队列都会收到同一份消息。
     * <p>Fanout 下 routingKey 会被 Broker 忽略，传空串即可。</p>
     *
     * @param msg 消息内容
     */
    public void sendFanoutMessage(String msg) {
        log.info("发送扇出消息: {}", msg);
        rabbitTemplate.convertAndSend(RabbitMqConfig.FANOUT_EXCHANGE, "", msg);
    }

    /**
     * 带 {@link CorrelationData} 发送，便于与 ConfirmCallback 中的 correlationId 对应。
     * <p>
     * routingKey 必须与消息体类型一致：字符串用
     * {@link RabbitMqConfig#ROUTING_KEY_DIRECT_STRING}，对象消息用 object 路由键，
     * 否则消费者反序列化会失败。
     * </p>
     *
     * @param routingKey    直连路由键
     * @param msg           字符串消息体
     * @param correlationId 业务侧关联 ID
     */
    public void sendWithConfirm(String routingKey, String msg, String correlationId) {
        log.info("发送确认消息, correlationId={}, routingKey={}, msg={}", correlationId, routingKey, msg);
        CorrelationData correlationData = new CorrelationData(correlationId);
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, routingKey, msg, correlationData);
    }

    /**
     * 显式声明消息为 PERSISTENT（教学对比用）。
     * <p>
     * 可靠落盘需同时满足：队列 durable=true，且消息 delivery mode=PERSISTENT。
     * Spring {@link RabbitTemplate#convertAndSend} 默认已是 PERSISTENT，本方法仅演示显式设置。
     * </p>
     *
     * @param msg 消息内容
     */
    public void sendPersistentMessage(String msg) {
        log.info("发送持久化消息: {}", msg);
        MessagePostProcessor processor = (Message message) -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        };
        rabbitTemplate.convertAndSend(RabbitMqConfig.DIRECT_EXCHANGE, RabbitMqConfig.ROUTING_KEY_DIRECT_STRING, msg,
            processor);
    }

    /**
     * 延迟投递：TTL + 无消费者延迟队列 + 死信转发到业务队列。
     * <p>
     * 链路：延迟交换机 → 延迟队列（等待 expirationMs）→ TTL 到期死信转发 →
     * 直连字符串路由 → {@link RabbitMqConfig#DIRECT_QUEUE_STRING}。
     * </p>
     *
     * @param msg          消息内容
     * @param expirationMs 延迟毫秒数，如 5000 表示约 5 秒后进入业务队列
     */
    public void sendDelayMessage(String msg, long expirationMs) {
        log.info("发送延迟消息, delay={}ms, msg={}", expirationMs, msg);
        MessagePostProcessor processor = (Message message) -> {
            // 在延迟队列中等待，到期触发死信转发（非“业务队列上的延迟消费”）
            message.getMessageProperties().setExpiration(String.valueOf(expirationMs));
            return message;
        };
        rabbitTemplate.convertAndSend(RabbitMqConfig.DELAY_EXCHANGE, RabbitMqConfig.ROUTING_KEY_DELAY, msg, processor);
    }

    /**
     * 批量构造并发送订单消息（压测/演示）。
     *
     * @param count 发送条数
     */
    public void batchSend(int count) {
        for (int i = 0; i < count; i++) {
            OrderMessage order = OrderMessage.builder()
                .orderId("ORDER_" + System.currentTimeMillis() + "_" + i)
                .userId(1000L + i)
                .amount(new BigDecimal("99.99"))
                .status("CREATED")
                .createTime(LocalDateTime.now())
                .build();
            sendOrderMessage(order);
        }
    }
}
