package com.gqtian.netty.web;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gqtian.common.annotation.GlobalApiOut;
import com.gqtian.common.globalProcess.GlobalException;
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