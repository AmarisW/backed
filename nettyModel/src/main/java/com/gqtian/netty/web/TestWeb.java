package com.gqtian.netty.web;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.common.annotation.GlobalApiOut;
import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.netty.service.TestService;
import com.gqtian.netty.utils.MessageUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 测试控制类
 * 
 * @className TestWeb
 * @author gqtian
 * @date 2025/8/7 14:44
 */
@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestWeb {

    /**
     * 发送消息接口
     * <p>
     * 通过Netty客户端连接指定主机和端口，发送指定消息
     * </p>
     *
     * @param host 主机地址
     * @param port 端口号
     * @param msg  待发送的消息内容
     * @throws Exception 网络连接或发送过程中可能出现的异常
     */
    @GlobalApiOut(value = "消息发送")
    @GetMapping("/sendMsg")
    public void sendMsg(String host, Integer port, String msg) throws Exception {
        TestService.SEND_MSG = msg;
        new TestService().runClient(host, port);
    }

    /**
     * 消息内容修改接口
     * <p>
     * 修改待发送的消息内容
     * </p>
     *
     * @param msg 新的消息内容
     */
    @GlobalApiOut(value = "消息内容修改")
    @GetMapping("/editMsg")
    public String editMsg(String msg) {
        TestService.SEND_MSG = msg;
        return TestService.SEND_MSG;
    }

    /**
     * 消息解码接口
     * <p>
     * 将传入的十六进制字符串消息进行解码处理，还原为原始字符串
     * </p>
     *
     * @param msg 待解码的十六进制字符串消息
     * @return java.lang.String 解码后的原始字符串
     */
    @GlobalApiOut(value = "消息解码")
    @GetMapping("/decode")
    public String decode(String msg) {
        if (StringUtils.hasLength(msg)) {
            return MessageUtil.decodeMsg(msg);
        } else {
            throw new GlobalException("请输入待解码的消息字符串");
        }
    }

    /**
     * 消息编码接口
     * <p>
     * 将传入的字符串消息进行编码处理，转换为十六进制字符串
     * </p>
     *
     * @param msg 待编码的消息字符串
     * @return java.lang.String 编码后的十六进制字符串
     */
    @GlobalApiOut(value = "消息编码")
    @GetMapping("/encode")
    public String encode(String msg) {
        if (StringUtils.hasLength(msg)) {
            return MessageUtil.encodeMsg(msg);
        } else {
            throw new GlobalException("请输入待编码的消息字符串");
        }
    }

}