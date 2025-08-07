package com.gqtian.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.gqtian.common.config.GlobalLaunchApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching // 开启缓存
@SpringBootApplication
public class AnnotationModelApplication {
    
    public static void main(String[] args) {
        GlobalLaunchApplicationContext.launch(AnnotationModelApplication.class, args);
    }
}