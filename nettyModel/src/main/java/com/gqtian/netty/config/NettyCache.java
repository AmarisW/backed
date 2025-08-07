package com.gqtian.netty.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;

/**
 * 👻 连接缓存
 * 
 * @className NettyCache
 * @author gqtian
 * @date 2025/8/7 11:11
 */
@Component
public class NettyCache {
    
    /**
     * 客户端缓存
     */
    private static final Map<String, Channel> CLIENT_CHANNEL_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 存入客户端缓存
     */
    public static void putClientChannel(String key, Channel channel) {
        CLIENT_CHANNEL_CACHE.put(key, channel);
    }
    
    /**
     * 获取全部的客户端缓存
     */
    public static Map<String, Channel> getClientChannelList() {
        return CLIENT_CHANNEL_CACHE;
    }
    
    /**
     * 移除客户端缓存
     */
    public static void removeClientChannel(String key) {
        CLIENT_CHANNEL_CACHE.remove(key);
    }
}
