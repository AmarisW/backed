package com.gqtian.hbase.service;

import java.util.List;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gqtian.hbase.entity.GpsData;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤔 生产者
 *
 * @author gqtian
 * @date 2023/07/24 09:55
 */
@Slf4j
@Service
public class ProducerService {

    @Value("${rocketmq.producer.topic}")
    private String topic;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendAsyncMsg(List<GpsData> msgBody) {
        // TODO 待完善
    }
}
