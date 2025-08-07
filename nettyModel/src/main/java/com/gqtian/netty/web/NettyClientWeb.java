package com.gqtian.netty.web;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.netty.config.NettyCache;
import com.gqtian.netty.server.NettyClient;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 Netty控制器
 * 
 * @className NettyWeb
 * @author gqtian
 * @date 2025/8/7 13:23
 */
@Slf4j
@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class NettyClientWeb {
    
    private final NettyClient nettyClient;
    
    /**
     * 启动一个客户端
     * 
     * @param serverIp 服务端地址
     * @param serverPort 服务端端口
     */
    @RequestMapping("/start")
    public String startClient(String serverIp, Integer serverPort) {
        return nettyClient.runClient(serverIp, serverPort);
    }
    
    /**
     * 关闭指定客户端
     */
    @RequestMapping("/stop")
    public void stopClient(String key) {
        Channel channel = NettyCache.getClientChannelList().get(key);
        if (channel != null) {
            channel.close();
            NettyCache.removeClientChannel(key);
        }
    }
    
    /**
     * 获取客户端状态
     */
    @RequestMapping("/status")
    public Map<String, Channel> getClientStatus() {
        return NettyCache.getClientChannelList();
    }
    
}
