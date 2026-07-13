package com.gqtian.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ 核心配置
 * <p>
 * 包含：RabbitTemplate、监听器容器工厂、各种 Exchange/Queue/Binding 声明
 * </p>
 * <p>
 * 主机、端口、用户等基础配置在 application.yml 的 spring.rabbitmq.* 中配置
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@Configuration
public class RabbitMqConfig {

    // ============================ 常量：交换机/队列/路由键 ============================

    /** 直连交换机 */
    public static final String DIRECT_EXCHANGE = "rabbit.direct.exchange";

    /** 主题交换机 */
    public static final String TOPIC_EXCHANGE = "rabbit.topic.exchange";

    /** 扇出交换机 */
    public static final String FANOUT_EXCHANGE = "rabbit.fanout.exchange";

    /** 直连队列（字符串消息，演示用） */
    public static final String DIRECT_QUEUE_STRING = "rabbit.direct.queue.string";

    /** 直连队列（对象消息，演示死信路由） */
    public static final String DIRECT_QUEUE_OBJECT = "rabbit.direct.queue.object";

    /** 主题队列（匹配 order.#） */
    public static final String TOPIC_QUEUE = "rabbit.topic.queue";

    /** 扇出队列 A */
    public static final String FANOUT_QUEUE_A = "rabbit.fanout.queue.a";

    /** 扇出队列 B */
    public static final String FANOUT_QUEUE_B = "rabbit.fanout.queue.b";

    /** 死信交换机 */
    public static final String DLX_EXCHANGE = "rabbit.dlx.exchange";

    /** 死信队列 */
    public static final String DLX_QUEUE = "rabbit.dlx.queue";

    /** 直连路由键（字符串消息） */
    public static final String ROUTING_KEY_DIRECT_STRING = "direct.routing.key.string";

    /** 直连路由键（对象消息） */
    public static final String ROUTING_KEY_DIRECT_OBJECT = "direct.routing.key.object";

    public static final String ROUTING_KEY_TOPIC = "order.#";

    public static final String ROUTING_KEY_DLX = "dlx.routing.key";

    // ============================ 消息转换器 ============================

    @Bean
    public MessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // ============================ RabbitTemplate ============================

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        // 开启 mandatory，触发回调
        template.setMandatory(true);
        // 确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息发送失败, correlationId={}, cause={}", correlationData != null ? correlationData.getId() : "null", cause);
            } else {
                log.info("消息发送成功, correlationId={}", correlationData != null ? correlationData.getId() : "null");
            }
        });
        // 返回回调（路由不到队列时触发）
        template.setReturnsCallback(returned -> log.error("消息路由失败, replyCode={}, replyText={}, exchange={}, routingKey={}",
            returned.getReplyCode(), returned.getReplyText(), returned.getExchange(), returned.getRoutingKey()));
        return template;
    }

    // ============================ 监听器容器工厂 ============================

    /**
     * 自定义监听器容器工厂
     * <p>
     * 通过 Spring Boot 的 {@link RabbitListenerContainerFactoryConfigurer} 自动应用 application.yml 中
     * spring.rabbitmq.listener.simple.* 的全部配置（acknowledge-mode、prefetch、concurrency、retry 等），
     * 此处仅需额外设置消息转换器即可。
     * </p>
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        RabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory,
        MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        // 覆盖 configurer 默认的 SimpleMessageConverter，让监听器支持 JSON 反序列化
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    // ============================ 交换机 ============================

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    // ============================ 队列 ============================

    /**
     * 直连-字符串队列：演示基础直连模式，不配置死信。
     */
    @Bean
    public Queue directQueueString() {
        return new Queue(DIRECT_QUEUE_STRING, true);
    }

    /**
     * 直连-对象队列：演示 nack(requeue=false) 时的死信路由。
     * <p>
     * 通过 x-dead-letter-exchange / x-dead-letter-routing-key 将被拒绝、过期、溢出的消息转发到 DLX。
     * </p>
     */
    @Bean
    public Queue directQueueObject() {
        return new Queue(DIRECT_QUEUE_OBJECT, true, false, false,
            java.util.Map.of("x-dead-letter-exchange", DLX_EXCHANGE, "x-dead-letter-routing-key", ROUTING_KEY_DLX));
    }

    @Bean
    public Queue topicQueue() {
        return new Queue(TOPIC_QUEUE, true);
    }

    @Bean
    public Queue fanoutQueueA() {
        return new Queue(FANOUT_QUEUE_A, true);
    }

    @Bean
    public Queue fanoutQueueB() {
        return new Queue(FANOUT_QUEUE_B, true);
    }

    @Bean
    public Queue dlxQueue() {
        return new Queue(DLX_QUEUE, true);
    }

    // ============================ 绑定 ============================

    @Bean
    public Binding directBindingString(Queue directQueueString, DirectExchange directExchange) {
        return BindingBuilder.bind(directQueueString).to(directExchange).with(ROUTING_KEY_DIRECT_STRING);
    }

    @Bean
    public Binding directBindingObject(Queue directQueueObject, DirectExchange directExchange) {
        return BindingBuilder.bind(directQueueObject).to(directExchange).with(ROUTING_KEY_DIRECT_OBJECT);
    }

    @Bean
    public Binding topicBinding(Queue topicQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicQueue).to(topicExchange).with(ROUTING_KEY_TOPIC);
    }

    @Bean
    public Binding fanoutBindingA(Queue fanoutQueueA, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueueA).to(fanoutExchange);
    }

    @Bean
    public Binding fanoutBindingB(Queue fanoutQueueB, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueueB).to(fanoutExchange);
    }

    @Bean
    public Binding dlxBinding(Queue dlxQueue, DirectExchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with(ROUTING_KEY_DLX);
    }
}
