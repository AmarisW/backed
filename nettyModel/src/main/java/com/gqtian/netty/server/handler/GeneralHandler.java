package com.gqtian.netty.server.handler;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻
 * 
 * @className ClientHandler
 * @author gqtian
 * @date 2025/8/7 11:13
 */
@Slf4j
@Component
public class GeneralHandler extends ChannelInboundHandlerAdapter {
    
    /**
     * ⏰ 用户事件触发处理 主要处理空闲状态事件（读空闲、写空闲、全空闲）
     *
     * @param ctx 通道处理上下文
     * @param evt 触发的事件对象
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        // 判断是否为空闲状态事件
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent)evt;
            
            switch (event.state()) {
                case READER_IDLE:
                    // 读空闲处理
                    log.warn("⏱️ 超时未读，客户端主动断开连接:【{}】", ctx.channel().remoteAddress());
                    ctx.close();
                    log.info("📴 读空闲连接已关闭: {}", ctx.channel().remoteAddress());
                    break;
                
                case WRITER_IDLE:
                    // 写空闲处理
                    log.warn("⏱️ 超时未写，客户端主动断开连接:【{}】", ctx.channel().remoteAddress());
                    ctx.close();
                    log.info("📴 写空闲连接已关闭: {}", ctx.channel().remoteAddress());
                    break;
                
                case ALL_IDLE:
                    // 全空闲处理（读写都空闲）
                    log.warn("⏱️ 超时未读写，客户端主动断开连接:【{}】", ctx.channel().remoteAddress());
                    ctx.close();
                    log.info("📴 全空闲连接已关闭: {}", ctx.channel().remoteAddress());
                    break;
                
                default:
                    log.warn("❓ 未知的空闲状态: {}", event.state());
                    break;
            }
        }
    }
    
    /**
     * 📝 通道可写性变化时触发 当通道的可写状态发生变化时调用此方法，用于监控写入缓冲区状态
     *
     * @param ctx 通道处理上下文
     */
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        Channel sendChannel = ctx.channel();
        
        // 检查通道是否仍然活跃
        if (!sendChannel.isActive()) {
            log.error("🔗 链路失活，remoteAddress：{}", sendChannel.remoteAddress());
            return;
        }
        
        // 判断并记录通道状态
        judgementChannel(sendChannel);
    }
    
    /**
     * 📊 评估并打印当前通道写入池状态 通过获取通道的高低水位配置和缓冲区状态，输出详细的写入状态信息
     *
     * @param sendChannel 发送数据的通道
     */
    private void judgementChannel(Channel sendChannel) {
        // 获取写入缓冲区的高低水位配置
        int writeBufferHighWaterMark = sendChannel.config().getWriteBufferHighWaterMark();
        int writeBufferLowWaterMark = sendChannel.config().getWriteBufferLowWaterMark();
        
        // 获取进入不可写/可写状态需要消耗的字节数
        long unWritable = sendChannel.bytesBeforeUnwritable(); // 进入不可写状态需要消耗的字节
        long writable = sendChannel.bytesBeforeWritable(); // 进入可写状态需要消耗的字节
        
        // 输出详细的写入状态日志（转换为KB单位便于阅读）
        log.info("📊 通道写入状态监控 - " + "高水位：【{} KB】，低水位：【{} KB】，当前写入状态：【{}】，" + "剩余缓存：【{} KB】可进入不可写状态，需要消耗：【{} KB】可进入可写状态",
            writeBufferHighWaterMark / 1024,
            writeBufferLowWaterMark / 1024,
            sendChannel.isWritable() ? "可写" : "不可写",
            unWritable / 1024,
            writable / 1024);
    }
}
