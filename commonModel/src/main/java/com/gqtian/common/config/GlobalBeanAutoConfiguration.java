package com.gqtian.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

import com.gqtian.common.initialization.DicInitRun;
import com.gqtian.common.utils.SpringApplicationUtil;

/**
 * 👻 自动配置类
 * 
 * @className BeanAutoConfiguration
 * @author gqtian
 * @date 2025/7/25 10:53
 */
@AutoConfiguration
@Import(
{DicInitRun.class, SpringApplicationUtil.class, GlobalMyBatisConfiguration.class})
public class GlobalBeanAutoConfiguration {
    
}
