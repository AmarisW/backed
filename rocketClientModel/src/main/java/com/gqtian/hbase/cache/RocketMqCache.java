package com.gqtian.hbase.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * 👻 mq相关缓存
 *
 * @author gqtian
 * @className RocketMqCache
 */
@Slf4j
@Component
public class RocketMqCache {

    /**
     * 生产者缓存信息
     */
    public static final Map<String, DefaultMQProducer> producerMap = new ConcurrentHashMap<>();

    /**
     * 消费者缓存信息
     */
    public static final Map<String, DefaultMQPushConsumer> consumerMap = new ConcurrentHashMap<>();

    /**
     * 根据标识获取生产者
     */
    public DefaultMQProducer getProducer(String producerId) {
        return producerMap.get(producerId);
    }

    public void putProducer(String producerId, DefaultMQProducer producer) {
        producerMap.put(producerId, producer);
    }

    public void removeProducer(String producerId) {
        producerMap.remove(producerId);
    }

    public DefaultMQPushConsumer getConsumer(String groupName) {
        return consumerMap.get(groupName);
    }

    public void putConsumer(String groupName, DefaultMQPushConsumer consumer) {
        consumerMap.put(groupName, consumer);
    }

    public void removeConsumer(String groupName) {
        consumerMap.remove(groupName);
    }

    /**
     * 获取全部信息
     */
    public Map<String, DefaultMQProducer> getProducers() {
        log.info("当前缓存生产者信息数:{},{}", producerMap.size(), JSON.toJSON(producerMap));
        return producerMap;
    }

    public Map<String, DefaultMQPushConsumer> getConsumers() {
        log.info("当前缓存消费者者信息数:{},{}", consumerMap.size(), JSON.toJSON(consumerMap));
        return consumerMap;
    }
}
