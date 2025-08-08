package com.gqtian.netty.server.codec;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 解码器
 * 
 * @className MessageDecoder
 * @author gqtian
 * @date 2025/8/6 14:15
 */
@Slf4j
public class MessageDecoder extends MessageToMessageDecoder<String> {
    
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, String object, List<Object> list) {
        // 打印输入数据
        log.info("输入数据：【{}】", object);
    }
}
