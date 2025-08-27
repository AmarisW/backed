package com.gqtian.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * 🔔 SpringUtil:用于加载Spring容器；静态方法中 调用Spring的Bean的方法 以静态变量保存Spring ApplicationContext,
 * 可在任何代码任何地方任何时候中取出-ApplicationContext
 *
 * @author GQTIAN
 * @date 2025/7/24 17:29
 **/
@Component
public class SpringApplicationUtil implements ApplicationContextAware {

    /**
     * -- GETTER -- 获取applicationContext
     *
     */
    // Spring应用上下文环境
    @Getter
    private static ApplicationContext applicationContext;

    /**
     * 通过name获取 Bean.
     *
     * @param name bean的名字
     * @return bean
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz bean的类
     * @return bean
     */
    public static <T> T getBean(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name bean的名字
     * @param clazz bean的类
     * @return bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringApplicationUtil.applicationContext == null) {
            SpringApplicationUtil.applicationContext = applicationContext;
        }
    }
}