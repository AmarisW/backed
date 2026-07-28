package com.gqtian.rabbit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * RabbitMQ 拓扑与客户端核心配置。
 * <p>
 * 拓扑按「交换机 → 队列 → 绑定」成组声明，便于对照阅读；
 * 另配置 {@link RabbitTemplate}（发布确认、路由失败回退）与监听器容器工厂（JSON、yml ACK/并发等）。
 * 连接信息见 {@code application-base.yml} 的 {@code spring.rabbitmq.*}。
 * </p>
 *
 * @author gqtian
 */
@Slf4j
@Configuration
public class RabbitMqConfig {

    // ============================ 常量（按场景分组） ============================

    // ----- 直连 -----
    /**
     * 直连交换机
     */
    public static final String DIRECT_EXCHANGE = "rabbit.direct.exchange";
    /**
     * 直连-字符串队列（亦为延迟消息到期后的目标队列）
     */
    public static final String DIRECT_QUEUE_STRING = "rabbit.direct.queue.string";
    /**
     * 直连-对象队列（配置死信，演示 nack 进 DLX）
     */
    public static final String DIRECT_QUEUE_OBJECT = "rabbit.direct.queue.object";
    /**
     * 直连路由键：字符串
     */
    public static final String ROUTING_KEY_DIRECT_STRING = "direct.routing.key.string";
    /**
     * 直连路由键：对象（OrderMessage）
     */
    public static final String ROUTING_KEY_DIRECT_OBJECT = "direct.routing.key.object";

    // ----- 主题 -----
    /**
     * 主题交换机
     */
    public static final String TOPIC_EXCHANGE = "rabbit.topic.exchange";
    /**
     * 主题队列
     */
    public static final String TOPIC_QUEUE = "rabbit.topic.queue";
    /**
     * 主题绑定键：order 及多级后缀
     */
    public static final String ROUTING_KEY_TOPIC = "order.#";

    // ----- 扇出 -----
    /**
     * 扇出交换机
     */
    public static final String FANOUT_EXCHANGE = "rabbit.fanout.exchange";
    /**
     * 扇出队列 A
     */
    public static final String FANOUT_QUEUE_A = "rabbit.fanout.queue.a";
    /**
     * 扇出队列 B
     */
    public static final String FANOUT_QUEUE_B = "rabbit.fanout.queue.b";

    // ----- 延迟（TTL + 死信转发到直连字符串队列） -----
    /**
     * 延迟交换机
     */
    public static final String DELAY_EXCHANGE = "rabbit.delay.exchange";
    /**
     * 延迟队列（无消费者）
     */
    public static final String DELAY_QUEUE = "rabbit.delay.queue";
    /**
     * 延迟路由键
     */
    public static final String ROUTING_KEY_DELAY = "delay.routing.key";

    // ----- 死信（承接对象队列拒绝/过期等） -----
    /**
     * 死信交换机
     */
    public static final String DLX_EXCHANGE = "rabbit.dlx.exchange";
    /**
     * 死信队列
     */
    public static final String DLX_QUEUE = "rabbit.dlx.queue";
    /**
     * 死信路由键
     */
    public static final String ROUTING_KEY_DLX = "dlx.routing.key";

    // ============================ 客户端：转换器 / Template / 监听工厂 ============================

    /**
     * JSON 消息转换器，生产与消费共用。
     *
     * @param objectMapper Spring Boot 自动配置的 Jackson（含 JavaTime 等）
     * @return Jackson2 JSON 转换器
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * 自定义 {@link RabbitTemplate}：mandatory + 确认/回退回调。
     * <p>
     * {@code mandatory=true} 时路由不到队列会触发 ReturnsCallback（需 yml {@code publisher-returns: true}）。
     * </p>
     *
     * @param connectionFactory 连接工厂
     * @param messageConverter  JSON 转换器
     * @return 已配置回调的 Template
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        // 无法路由时触发 ReturnsCallback，避免静默丢弃
        template.setMandatory(true);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息发送失败, correlationId={}, cause={}",
                        correlationData != null ? correlationData.getId() : "null", cause);
            } else {
                log.info("消息发送成功, correlationId={}",
                        correlationData != null ? correlationData.getId() : "null");
            }
        });
        template.setReturnsCallback(returned -> log.error(
                "消息路由失败, replyCode={}, replyText={}, exchange={}, routingKey={}",
                returned.getReplyCode(), returned.getReplyText(), returned.getExchange(), returned.getRoutingKey()));
        return template;
    }

    /**
     * 监听器容器工厂：沿用 yml 的 simple 配置，仅覆盖消息转换器。
     *
     * @param configurer        Boot simple 工厂配置器
     * @param connectionFactory 连接工厂
     * @param messageConverter  JSON 转换器
     * @return 监听器容器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer, ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    // ============================ 直连：交换机 + 字符串/对象队列 + 绑定 ============================

    /**
     * 持久化直连交换机（字符串队列、对象队列共用）
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE, true, false);
    }

    /**
     * 字符串业务队列。
     * <p>未配置 DLX：{@code nack(requeue=false)} 直接丢弃；亦接收延迟队列到期转发的消息。</p>
     */
    @Bean
    public Queue directQueueString() {
        return new Queue(DIRECT_QUEUE_STRING, true);
    }

    /**
     * 字符串队列 ↔ 直连交换机（入参为交换机和队列的方法名）
     */
    @Bean
    public Binding directBindingString(Queue directQueueString, DirectExchange directExchange) {
        return BindingBuilder.bind(directQueueString).to(directExchange).with(ROUTING_KEY_DIRECT_STRING);
    }

    /**
     * 对象业务队列：拒绝/过期/溢出时转发到死信交换机。
     * <p>{@code x-dead-letter-*} 指向 {@link #DLX_EXCHANGE} / {@link #ROUTING_KEY_DLX}。</p>
     */
    @Bean
    public Queue directQueueObject() {
        return new Queue(DIRECT_QUEUE_OBJECT, true, false, false,
                Map.of("x-dead-letter-exchange", DLX_EXCHANGE, "x-dead-letter-routing-key", ROUTING_KEY_DLX));
    }

    /**
     * 对象队列 ↔ 直连交换机
     */
    @Bean
    public Binding directBindingObject(Queue directQueueObject, DirectExchange directExchange) {
        return BindingBuilder.bind(directQueueObject).to(directExchange).with(ROUTING_KEY_DIRECT_OBJECT);
    }

    // ============================ 主题：交换机 + 队列 + 绑定 ============================

    /**
     * 持久化主题交换机
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE, true, false);
    }

    /**
     * 主题队列
     */
    @Bean
    public Queue topicQueue() {
        return new Queue(TOPIC_QUEUE, true);
    }

    /**
     * 主题队列 ↔ 主题交换机（order.#）
     */
    @Bean
    public Binding topicBinding(Queue topicQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicQueue).to(topicExchange).with(ROUTING_KEY_TOPIC);
    }

    // ============================ 扇出：交换机 + 双队列 + 绑定 ============================

    /**
     * 持久化扇出交换机
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE, true, false);
    }

    /**
     * 扇出队列 A
     */
    @Bean
    public Queue fanoutQueueA() {
        return new Queue(FANOUT_QUEUE_A, true);
    }

    /**
     * 扇出队列 A ↔ 扇出交换机
     */
    @Bean
    public Binding fanoutBindingA(Queue fanoutQueueA, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueueA).to(fanoutExchange);
    }

    /**
     * 扇出队列 B
     */
    @Bean
    public Queue fanoutQueueB() {
        return new Queue(FANOUT_QUEUE_B, true);
    }

    /**
     * 扇出队列 B ↔ 扇出交换机
     */
    @Bean
    public Binding fanoutBindingB(Queue fanoutQueueB, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueueB).to(fanoutExchange);
    }

    // ============================ 延迟：交换机 + 队列 + 绑定 ============================

    /**
     * 延迟交换机：只把消息送入无消费者的延迟队列。
     */
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE, true, false);
    }

    /**
     * 延迟队列：per-message TTL 到期后死信转发到直连字符串路由。
     * <p>最终进入 {@link #DIRECT_QUEUE_STRING}；勿把 expiration 直接打在有消费者的业务队列上当延迟用。</p>
     */
    @Bean
    public Queue delayQueue() {
        return new Queue(DELAY_QUEUE, true, false, false,
                Map.of("x-dead-letter-exchange", DIRECT_EXCHANGE, "x-dead-letter-routing-key", ROUTING_KEY_DIRECT_STRING));
    }

    /**
     * 延迟队列 ↔ 延迟交换机
     */
    @Bean
    public Binding delayBinding(Queue delayQueue, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayQueue).to(delayExchange).with(ROUTING_KEY_DELAY);
    }

    // ============================ 死信：交换机 + 队列 + 绑定 ============================

    /**
     * 持久化死信交换机（Direct）
     */
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE, true, false);
    }

    /**
     * 死信队列；无更外层 DLX，处理失败需谨慎 nack。
     */
    @Bean
    public Queue dlxQueue() {
        return new Queue(DLX_QUEUE, true);
    }

    /**
     * 死信队列 ↔ 死信交换机
     */
    @Bean
    public Binding dlxBinding(Queue dlxQueue, DirectExchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with(ROUTING_KEY_DLX);
    }
}
