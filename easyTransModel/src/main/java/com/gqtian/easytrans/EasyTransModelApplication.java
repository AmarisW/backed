package com.gqtian.easytrans;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.gqtian.common.config.GlobalBeautifyStart;

import lombok.extern.slf4j.Slf4j;

/**
 * Easy-Trans 数据翻译框架学习模块启动类
 *
 * @author gqtian
 */
@Slf4j
@SpringBootApplication
public class EasyTransModelApplication {

    public static void main(String[] args) {
        GlobalBeautifyStart.start(EasyTransModelApplication.class, args);
    }
}
