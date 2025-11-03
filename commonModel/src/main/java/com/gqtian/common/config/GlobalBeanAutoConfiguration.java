package com.gqtian.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

import com.gqtian.common.globalProcess.GlobalExceptionAdvice;
import com.gqtian.common.globalProcess.GlobalResponseBodyAdvice;
import com.gqtian.common.initialization.GlobalDictInitRun;
import com.gqtian.common.utils.SpringApplicationUtil;
import com.gqtian.common.web.GlobalExposedController;

/**
 * 👻 自动配置类
 * 
 * @className BeanAutoConfiguration
 * @author gqtian
 * @date 2025/7/25 10:53
 */
@AutoConfiguration
@Import({GlobalResponseBodyAdvice.class, // 全局数据返回处理
    GlobalExceptionAdvice.class, // 全局异常处理
    GlobalApiInfoScanner.class, // 扫描接口文档
    GlobalMyBatisConfiguration.class, // 配置MyBatis
    GlobalExposedController.class, // 全局开放Api列表
    SpringApplicationUtil.class, // SpringApplicationUtil
    GlobalDictInitRun.class // 初始化字典
})
public class GlobalBeanAutoConfiguration {}
