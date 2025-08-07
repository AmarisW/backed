package com.gqtian.netty.server;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.netty.config.NettyConfig;
import com.gqtian.netty.server.init.InitClientChannel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gqtian
 * @description: netty启动类
 * @date 2018-12-27 12:52
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NettyClient {
    
    private final NettyConfig nettyConfig;
    
    private final InitClientChannel initClientChannel;
    
    /**
     * 主链路（服务端）引导入口
     */
    public String runClient(String serverIp, Integer serverPort) {
        
        // 获取服务端地址和端口
        if (!StringUtils.hasLength(serverIp) && serverPort == null) {
            serverIp = nettyConfig.getClient().getServerIp();
            serverPort = nettyConfig.getClient().getServerPort();
        }
        
        // 创建客户端引导类
        Bootstrap bootStrapClient = new Bootstrap();
        // 在netty中考虑到代码的统一性，也允许你在客户端设置线程池
        NioEventLoopGroup group = new NioEventLoopGroup(nettyConfig.getBossMaxThreadCount());
        
        try {
            bootStrapClient.group(group)
                .channel(NioSocketChannel.class)
                .handler(initClientChannel)
                .option(ChannelOption.TCP_NODELAY, true);
            // .bind(1000); 客户端绑定固定端口
            
            // region 同步等待连接结果
            ChannelFuture channelFuture = bootStrapClient.connect(serverIp, serverPort).sync();
            log.info("【客户端】启动成功，链接地址：【{}:{}】", serverIp, serverPort);
            // endregion
            
            // region 异步监听连接结果
            // ChannelFuture channelFuture = bootStrapClient.connect(serverIp, serverPort).sync();
            // String finalServerIp = serverIp;
            // Integer finalServerPort = serverPort;
            // channelFuture.addListener(future -> {
            // if (future.isSuccess()) {
            // Channel clientChannel = channelFuture.channel();
            // String longText = clientChannel.id().asLongText();
            // log.info("【客户端】启动成功，链接地址：【{}:{}】", finalServerIp, finalServerPort);
            // NettyCache.putClientChannel(longText, clientChannel);
            // }
            // else {
            // log.error("【客户端】启动失败：{}", future.cause().getMessage(), future.cause());
            // }
            // });
            // endregion
            
            // 返回客户端通道的唯一标识
            return channelFuture.channel().id().asLongText();
        }
        catch (Exception e) {
            log.error("【客户端】启动失败：{}", e.getMessage(), e);
            throw new GlobalException("【客户端】启动失败：" + e.getMessage());
        }
    }
}
