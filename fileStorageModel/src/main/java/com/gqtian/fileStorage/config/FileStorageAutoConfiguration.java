/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gqtian.fileStorage.service.FileStorageService;
import com.gqtian.fileStorage.service.FileStorageServiceBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 文件存储自动配置类 当Spring上下文中没有FileStorageService bean时，会自动配置该服务 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnMissingBean(FileStorageService.class)
public class FileStorageAutoConfiguration {
    
    /**
     * 文件存储属性，用于配置文件存储服务
     */
    private final FileStorageProperties properties;
    
    /**
     * 创建并配置文件存储服务
     *
     * @return FileStorageService 文件存储服务实例
     */
    @Bean(destroyMethod = "destroy")
    public FileStorageService fileStorageServiceBuilder() {
        // 根据文件存储属性创建文件存储服务构建器
        FileStorageServiceBuilder fileStorageServiceBuilder = FileStorageServiceBuilder.create(properties);
        // 构建并返回文件存储服务实例
        return fileStorageServiceBuilder.build();
    }
}