package com.gqtian.mongodb;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.gqtian.common.config.GlobalBeautifyStart;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class MangoDBModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(MangoDBModelApplication.class, args);
    }
}
