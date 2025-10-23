package com.gqtian.netty;

import com.gqtian.netty.utils.ByteArrayUtil;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gqtian
 * @description:
 * @date 2019-03-13 15:02
 */
@Slf4j
public class NettyClientTestHandler extends ChannelInboundHandlerAdapter {

    /**
     * 连接成功后，向服务端发送消息
     *
     * @param ctx 上下文对象，含有 管道pipeline，通道channel，地址
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    /**
     * 读取服务端发送过来的消息
     *
     * @param ctx 上下文对象，含有 管道pipeline，通道channel，地址
     * @param msg 服务端发送过来的数据 默认Object
     * @throws Exception 异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("读取消息:{}", ByteArrayUtil.bytes2HexStr((byte[])msg));
        super.channelRead(ctx, msg);
    }

    /**
     * 数据读取完毕
     *
     * @param ctx 上下文对象，含有 管道pipeline，通道channel，地址
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}
