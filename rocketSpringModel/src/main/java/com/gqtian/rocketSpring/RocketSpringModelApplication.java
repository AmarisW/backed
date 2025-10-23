package com.gqtian.rocketSpring;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.gqtian.common.config.GlobalBeautifyStart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching // 开启缓存
@SpringBootApplication
public class RocketSpringModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(RocketSpringModelApplication.class, args);
    }
}