/* Copyright (C) 2020-present Linkcld. All rights reserved. */
package com.gqtian.fileStorage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.gqtian.fileStorage.config.FileStorageAutoConfiguration;
import com.gqtian.fileStorage.config.FileStorageProperties;

/** 启用文件存储，会自动根据配置文件进行加载 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{ElementType.TYPE})
@Documented
@Import(
{FileStorageProperties.class, FileStorageAutoConfiguration.class})
public @interface EnableFileStorage {
}
