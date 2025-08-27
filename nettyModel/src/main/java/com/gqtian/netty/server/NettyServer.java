package com.gqtian.netty.server;

import io.netty.buffer.PooledByteBufAllocator;
import org.springframework.stereotype.Service;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.netty.config.NettyConfig;
import com.gqtian.netty.server.init.InitServerChannel;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
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
public class NettyServer {
    
    private final NettyConfig nettyConfig;
    
    private final InitServerChannel initServerChannel;
    
    public static Channel serverChannel;
    
    /**
     * 主链路（服务端）引导入口
     */
    public void runServer() {
        
        if (serverChannel != null) {
            log.error("服务端已经启动！请勿重复启动！");
            throw new GlobalException("服务端已经启动！请勿重复启动！");
        }
        
        // 创建主线程池（管理Tcp连接线程池）
        NioEventLoopGroup boosGroup = new NioEventLoopGroup(nettyConfig.getBossMaxThreadCount());
        // 创建工作线程池（处理业务逻辑线程池）
        NioEventLoopGroup workGroup = new NioEventLoopGroup(nettyConfig.getWorkMaxThreadCount());
        
        // 创建一个服务器端的程序类进行NIO的启动，同时可以设置Channel
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        
        try {
            // 设置要使用的线程池以及当前的Channel 的类型
            serverBootstrap.group(boosGroup, workGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
            // 接收到的信息处理器
            serverBootstrap.childHandler(initServerChannel);
            // 设置内存分配器 - 用于接受连接的父通道
            serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            // 设置内存分配器 - 用于每个子通道（实际的数据通信）
            serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.option(ChannelOption.TCP_NODELAY, true);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 设置写缓冲区大小
            serverBootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK,
                new WriteBufferWaterMark(1048576, 1048576 * 2));
            // 同步等待启动成功
            ChannelFuture channelFuture = serverBootstrap.bind(nettyConfig.getServer().getPort()).sync();
            // region 异步检测启动结果
            // channelFuture.addListener(future -> {
            // if (future.isSuccess()) {
            // log.info("主链路启动成功，地址：【{}:{}】", "127.0.0.1", nettyConfig.getServer().getPort());
            // }
            // else {
            // log.error("主链路启动失败：", future.cause());
            // }
            // });
            // endregion
            serverChannel = channelFuture.channel();
            // 打印出启动信息
            log.info("【服务端】主链路启动成功，地址：【{}:{}】", "127.0.0.1", nettyConfig.getServer().getPort());
            /*
             * 同步阻塞等待 future.channel().closeFuture().sync();这个语句的主要目的是，如果缺失上述代码，则main方法所在的线程，即主线程会在执行完bind().sync()方法后，
             * 会进入finally 代码块，之前的启动的nettyServer也会随之关闭掉，整个程序都结束了。该语句能让线程进入wait状态，也就是main线程暂时不会执行到finally
             * 里面，nettyServer也持续运行， 如果监听到关闭事件，可以优雅的关闭通道和nettyServer， 如果你在代码中通过其他线程触发了关闭事件，此时main
             * 会响应事件，并进入finally代码块，进行优雅的关闭操作->shutdownGracefully。
             */
            serverChannel.closeFuture().sync();
        }
        catch (Exception e) {
            log.error("【服务端】启动失败：{}", e.getMessage(), e);
        }
        finally {
            workGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
            serverChannel.close();
        }
    }
    
    /**
     * 停止服务器
     */
    public void stopServer() {
        if (serverChannel != null) {
            serverChannel.close();
            serverChannel = null;
        }
    }
}
