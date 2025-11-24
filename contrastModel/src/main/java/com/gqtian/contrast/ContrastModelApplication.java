package com.gqtian.contrast;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.gqtian.common.config.GlobalBeautifyStart;
import com.gqtian.fileStorage.EnableFileStorage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching // 开启缓存
@SpringBootApplication // 启动类
@EnableFileStorage // 启动文件存储服务
@EnableAsync // 开启异步线程
@EnableScheduling // 开启定时任务
public class ContrastModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(ContrastModelApplication.class, args);
    }
}