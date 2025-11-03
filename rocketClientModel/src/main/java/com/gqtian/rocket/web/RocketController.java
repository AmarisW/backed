package com.gqtian.rocket.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.rocket.rmq.ConsumerService;
import com.gqtian.rocket.rmq.ProducerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Rocket控制台
 *
 * @author gqtian
 * @className RocketController
 * @date 2024/6/5 10:32
 */
@Slf4j
@RestController
@RequestMapping("/rocket")
@RequiredArgsConstructor
public class RocketController {

    private final ConsumerService consumerService;

    private final ProducerService producerService;

    /**
     * 启动一个消费者
     */
    @RequestMapping("/startConsumer")
    public void startConsumer(String rocketId) {
        log.info("启动一个消费者");
        consumerService.start(rocketId);
    }

    /**
     * 停止一个消费者
     */
    @RequestMapping("/stopConsumer")
    public void stopConsumer(String rocketId) {
        log.info("停止一个消费者");
        consumerService.shutdown(rocketId);
    }

    /**
     * 重启一个消费者
     */
    @RequestMapping("/restartConsumer")
    public void restartConsumer(String rocketId) {
        log.info("重启一个消费者");
        consumerService.restart(rocketId);
    }

    /**
     * 启动一个生产者
     */
    @RequestMapping("/startProducer")
    public void startProducer(String rocketId) {
        log.info("启动一个生产者");
        producerService.start(rocketId);
    }

    /**
     * 停止一个生产者
     */
    @RequestMapping("/stopProducer")
    public void stopProducer(String rocketId) {
        log.info("停止一个生产者");
        producerService.shutdown(rocketId);
    }

}
