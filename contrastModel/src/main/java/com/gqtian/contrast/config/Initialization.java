package com.gqtian.contrast.config;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 初始化操作 ： 处理项目中各类初始化操作
 *
 * @author gqtian
 * @className Initialization
 * @date 2024/5/30 11:17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Initialization {

    private final YmlProperties ymlProperties;

    @PostConstruct
    public void init() {
        log.info("初始化操作 YmlProperties ：{}", JSON.toJSON(ymlProperties));
    }

}
