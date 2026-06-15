package com.gqtian.rabbit;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.gqtian.common.config.GlobalBeautifyStart;
import com.gqtian.common.config.GlobalDatabaseAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(exclude = {GlobalDatabaseAutoConfiguration.class})
public class RabbitMqModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(RabbitMqModelApplication.class, args);
    }
}
