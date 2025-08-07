package com.gqtian.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 编码器
 * 
 * @className Message
 * @author gqtian
 * @date 2025/8/6 14:16
 */
@Slf4j
public class MessageEncoder extends MessageToByteEncoder<String> {
    
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String object, ByteBuf byteBuf) {
        
    }
}
