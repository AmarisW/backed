// TransmitHandler.java
package com.gqtian.netty.server.handler;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 🚀 通用消息处理 提供基础的通道处理功能，包括连接激活、异常处理、空闲检测和写入状态监控
 */
@Slf4j
@Component
public class ServerHandler extends GeneralHandler {
    /**
     * 🔧 通用消息处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("【服务端】🚀 收到消息：{}", msg);
    }

    /**
     * 🔧 通道激活时触发 当客户端连接成功建立时调用此方法
     *
     * @param ctx 通道处理上下文
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel channel = ctx.channel();
        String shortChannelId = channel.id().asShortText();
        // 记录连接建立日志
        log.info("【服务端】🔗 客户端连接已建立 - ChannelId: {}, RemoteAddress: {}", shortChannelId, channel.remoteAddress());
    }

    /**
     * 🔧 通道不活跃时触发 当客户端连接断开时调用此方法
     *
     * @param ctx 通道处理上下文
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        final Channel channel = ctx.channel();
        String shortChannelId = channel.id().asShortText();
        // 记录连接关闭日志
        log.info("【服务端】 {}, 地址: {}, ChannelId: {}", channel.remoteAddress(), channel.remoteAddress(), shortChannelId);
    }

    /**
     * ⚠️ 异常处理 捕获并处理通道中的异常情况
     *
     * @param ctx 通道处理上下文
     * @param e 抛出的异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        // 记录详细的异常信息
        log.error("【服务端】💥 发生异常:【{}】,原因:【{}】,错误信息【{}】", ctx.channel().remoteAddress(), e.getCause(), e.getMessage(), e);

        // 关闭异常连接
        ctx.close();

        // 记录连接关闭操作
        log.info("【服务端】📴 因异常已关闭连接: {}", ctx.channel().remoteAddress());
    }
}