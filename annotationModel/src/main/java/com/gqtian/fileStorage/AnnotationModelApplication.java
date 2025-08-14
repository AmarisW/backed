package com.gqtian.fileStorage;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.gqtian.common.config.GlobalBeautifyStart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching // 开启缓存
@SpringBootApplication
public class AnnotationModelApplication {
    
    public static void main(String[] args) {
        GlobalBeautifyStart.start(AnnotationModelApplication.class, args);
    }
}