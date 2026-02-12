package com.gqtian.netty;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.gqtian.common.config.GlobalBeautifyStart;
import com.gqtian.common.config.GlobalDatabaseAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching // 开启缓存
@SpringBootApplication(exclude = GlobalDatabaseAutoConfiguration.class)
public class NettyModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(NettyModelApplication.class, args);
    }
}