package com.gqtian.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

import com.gqtian.common.controller.GlobalExposedController;
import com.gqtian.common.globalProcess.GlobalExceptionAdvice;
import com.gqtian.common.globalProcess.GlobalResponseBodyAdvice;
import com.gqtian.common.utils.SpringApplicationUtil;

/**
 * 全局Bean自动配置类
 * <p>
 * 通过@Import注解自动注册全局公共组件，包括：
 * <ul>
 *   <li>GlobalResponseBodyAdvice - 全局响应体处理，统一封装返回结果</li>
 *   <li>GlobalExceptionAdvice - 全局异常处理，捕获并处理应用异常</li>
 *   <li>GlobalApiInfoScanner - API信息扫描器，扫描并打印接口信息</li>
 *   <li>GlobalExposedController - 全局开放API控制器</li>
 *   <li>SpringApplicationUtil - Spring应用上下文工具类</li>
 * </ul>
 * </p>
 * <p>
 * 注意：数据库相关配置已独立到 GlobalDatabaseAutoConfiguration
 * </p>
 * 
 * @author gqtian
 * @date 2025/7/25 10:53
 */
@AutoConfiguration
@Import({GlobalResponseBodyAdvice.class, // 全局数据返回处理
    GlobalExceptionAdvice.class, // 全局异常处理
    GlobalApiInfoScanner.class, // 扫描接口文档
    GlobalExposedController.class, // 全局开放Api列表
    SpringApplicationUtil.class // SpringApplicationUtil
})
public class GlobalBeanAutoConfiguration {}
