package com.gqtian.netty;

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
 * @author gqtian
 * @date 2019-03-13 14:40
 */
@Slf4j
public class NettyClientTest {

    public void runClient(String ip, Integer port, String msg) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(group);
            client.channel(NioSocketChannel.class);
            client.option(ChannelOption.TCP_NODELAY, true);
            client.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    /*
                     * socketChannel.pipeline().addLast(new Byte2MessageDecoder()); socketChannel.pipeline().addLast(new
                     * Message2ByteEncoder());
                     */
                    socketChannel.pipeline().addLast(new NettyClientTestHandler());
                }
            });
            ChannelFuture channelFuture = client.connect(ip, port).sync();
            Channel channel = channelFuture.channel();
            while (true) {
                final ByteBuf buffer = Unpooled.buffer();
                channel.writeAndFlush(buffer.writeBytes(HexUtil.decodeHex(msg)))
                    .addListener((ChannelFutureListener)future -> {
                        if (future.isSuccess()) {
                            log.info("发送数据：【{}】", msg);
                        }
                    });
                Thread.sleep(10000);
            }
        } finally {
            group.shutdownGracefully(); // 作用：优雅关闭线程池
        }
    }

}
