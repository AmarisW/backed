package com.gqtian.rocketSpring.init;

import com.gqtian.rocketSpring.cache.RocketMqCache;
import java.util.List;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson2.JSON;
import com.gqtian.rocketSpring.rmq.RocketClientProperties;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 生产者初始化
 *
 * @author gqtian
 * @className ProducerInit
 * @date 2024/5/13 13:27
 */
@Slf4j
@Component
@Order(9)
@AllArgsConstructor
public class ProducerInit implements ApplicationRunner {

    // 依赖注入
    private final RocketClientProperties rocketClientProperties;

    private final RocketMqCache rocketMqCache;

    @Override
    public void run(ApplicationArguments args) {
        List<RocketClientProperties.ProducerInfo> producerList = rocketClientProperties.getProducerList();
        if (CollectionUtils.isEmpty(producerList)) {
            log.info("未找到生产者配置信息,请检查配置!!!");
        }
        // 开始初始化生产者信息
        producerList.forEach(this::creatProducer);
    }

    /**
     * 创建并初始化RocketMQ的生产者对象。
     *
     * @param producerInfo 包含生产者配置信息的对象，如组名、nameserver地址等。 该信息用于配置生产者的各种行为。
     */
    public void creatProducer(RocketClientProperties.ProducerInfo producerInfo) {
        try {
            // 创建一个 DefaultMQProducer 实例并设置组名
            DefaultMQProducer producer = new DefaultMQProducer(producerInfo.getGroupName());
            // 设置NameServer地址
            producer.setNamesrvAddr(producerInfo.getNameSrvAddr());
            // 默认不使用VIP通道
            producer.setVipChannelEnabled(false);
            // 设置可发送消息的最大大小
            producer.setMaxMessageSize(producerInfo.getMaxMessageSize());
            // 设置发送消息的超时时间
            producer.setSendMsgTimeout(producerInfo.getSendMsgTimeOut());
            producer.setRetryTimesWhenSendAsyncFailed(
                // 设置异步发送失败时的重试次数
                producerInfo.getRetryTimesWhenSendFailed());
            producer.start();
            // 将生产者实例存储到缓存中
            rocketMqCache.putProducer(producerInfo.getProducerId(), producer);
            // 记录初始化成功的日志
            log.info("生产者初始化成功,生产者信息:{}", JSON.toJSON(producerInfo));
        } catch (MQClientException e) {
            log.error("生产者初始化失败,生产者信息:{}", JSON.toJSON(producerInfo), e);
        }
    }

}
