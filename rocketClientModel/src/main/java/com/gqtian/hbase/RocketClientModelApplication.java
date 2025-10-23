package com.gqtian.hbase;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.gqtian.common.config.GlobalBeautifyStart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching // 开启缓存
@SpringBootApplication
public class RocketClientModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(RocketClientModelApplication.class, args);
    }
}