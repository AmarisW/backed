package com.gqtian.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.gqtian.common.config.GlobalBeautifyStart;
import com.gqtian.common.config.GlobalDatabaseAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching // 开启缓存
@SpringBootApplication(exclude = {GlobalDatabaseAutoConfiguration.class})
public class SaTokenModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(SaTokenModelApplication.class, args);
    }
}