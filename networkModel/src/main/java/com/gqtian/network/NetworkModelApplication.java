package com.gqtian.network;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.gqtian.common.config.GlobalBeautifyStart;

@SpringBootApplication
public class NetworkModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(NetworkModelApplication.class, args);
    }

}
