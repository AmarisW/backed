package com.gqtian.netty.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.gqtian.netty.server.handler.ClientHandler;

import cn.hutool.core.util.HexUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 测试工具方法
 *
 * @author gqtian
 * @className TestService
 * @date 2025/8/8 14:41
 */
@Slf4j
@Service
public class TestService {

    public static String SEND_MSG = "";

    public void runClient(String ip, Integer port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(group);
            client.channel(NioSocketChannel.class);
            client.option(ChannelOption.TCP_NODELAY, true);
            client.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    socketChannel.pipeline().addLast(new ClientHandler());
                }
            });
            ChannelFuture channelFuture = client.connect(ip, port).sync();
            Channel channel = channelFuture.channel();
            while (true) {
                if (StringUtils.isEmpty(SEND_MSG)) {
                    final ByteBuf buffer = Unpooled.buffer();
                    channel.writeAndFlush(buffer.writeBytes(HexUtil.decodeHex(SEND_MSG)))
                        .addListener((ChannelFutureListener)future -> {
                            if (future.isSuccess()) {
                                log.info("测试发送数据：【{}】", SEND_MSG);
                            }
                        });
                }
                Thread.sleep(10000);
            }
        } finally {
            group.shutdownGracefully(); // 作用：优雅关闭线程池
        }
    }
}
