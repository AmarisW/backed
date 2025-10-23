package com.gqtian.contrast.demo.eventListenerDemo.util;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.gqtian.contrast.demo.eventListenerDemo.event.AbstractMessage;
import com.gqtian.contrast.demo.eventListenerDemo.event.MessageThree;

@Component
public class ContextUtil {

    private static ApplicationContext applicationContext;

    public ContextUtil(ApplicationContext applicationContext) {
        ContextUtil.applicationContext = applicationContext;
    }

    /**
     * 通过类名获取Bean
     */
    public static Object getBean(String className) throws ClassNotFoundException {
        return applicationContext.getBean(Class.forName(className));
    }

    /**
     * 发布消息
     *
     * @param messageRouteInfo 消息
     */
    public static void publishEvent(AbstractMessage messageRouteInfo) {
        applicationContext.publishEvent(messageRouteInfo);
    }

    /**
     * 发布消息
     *
     * @param messageThrees 消息
     */
    public static void publishEvent(List<MessageThree> messageThrees) {
        applicationContext.publishEvent(messageThrees);
    }

    /**
     * 发布消息
     *
     * @param messageThree 消息
     */
    public static void publishEvent(MessageThree messageThree) {
        applicationContext.publishEvent(messageThree);
    }
}
