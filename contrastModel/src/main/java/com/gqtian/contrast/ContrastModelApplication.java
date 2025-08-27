package com.gqtian.contrast;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.gqtian.common.config.GlobalBeautifyStart;
import com.gqtian.fileStorage.EnableFileStorage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching // 开启缓存
@SpringBootApplication
@EnableFileStorage
public class ContrastModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(ContrastModelApplication.class, args);
    }
}