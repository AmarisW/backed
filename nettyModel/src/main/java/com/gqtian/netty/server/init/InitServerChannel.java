package com.gqtian.netty.server.init;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.gqtian.netty.config.NettyConfig;
import com.gqtian.netty.server.codec.MessageDecoder;
import com.gqtian.netty.server.codec.MessageEncoder;
import com.gqtian.netty.server.handler.ServerHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.RequiredArgsConstructor;

/**
 * 🚀 服务端通道初始化器 负责为每个新建立的Socket连接配置处理管道(pipeline) 添加必要的编解码器和业务处理器到Netty处理链中
 *
 * @author gqtian
 * @date 2019-07-17 19:09
 */
@Component
@RequiredArgsConstructor
public class InitServerChannel extends ChannelInitializer<SocketChannel> {
    
    /**
     * 🔧 Netty配置类，通过构造函数自动注入 包含心跳检测时间等配置参数
     */
    private final NettyConfig nettyConfig;
    
    /**
     * 🛠️ 初始化通道的方法 当新的客户端连接建立时，Netty会自动调用此方法 为该连接配置处理管道，按顺序添加各种处理器
     *
     * @param socketChannel 新建立的Socket通道
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        
        // 添加消息编码器，用于将Java对象编码为字节流发送
        socketChannel.pipeline().addLast(new MessageEncoder());
        
        // 添加消息解码器，用于将接收到的字节流解码为Java对象
        socketChannel.pipeline().addLast(new MessageDecoder());
        
        // 添加心跳检测处理器，用于检测空闲连接
        // 参数分别为：读空闲时间、写空闲时间、所有类型空闲时间，时间单位
        socketChannel.pipeline()
            .addLast(new IdleStateHandler(nettyConfig.getReaderIdleTimeSeconds(),
                nettyConfig.getWriterIdleTimeSeconds(), nettyConfig.getAllIdleTimeSeconds(), TimeUnit.SECONDS));
        
        // 添加通用业务处理器，处理具体的业务逻辑
        socketChannel.pipeline().addLast(new ServerHandler());
    }
}
