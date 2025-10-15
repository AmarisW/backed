package com.gqtian.netty.web;

import java.util.LinkedHashMap;

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
            return MessageUtil.encodeMsg(msg).toLowerCase();

        } else {
            throw new GlobalException("请输入待编码的消息字符串");
        }
    }

    /**
     * GritLog 构建日志查询表达式
     */
    @GlobalApiOut(value = "GritLog 构建日志查询表达式")
    @GetMapping("/buildGritLogQuery")
    public LinkedHashMap<String, String> buildGritLogQuery(String msg) {
        LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
        String encodeMsg = MessageUtil.encodeMsg(msg).toLowerCase();
        String tem = "((测0000)&(拍照指令)|(0000000000000000)&(【1500|【9500))";
        String temFive = "(0000000000000000)&(【1500|【9500)";
        String temOne = "grep '0000000000000000' | grep '【1500'";
        String temTwo = "grep '0000000000000000' | grep '【9500'";
        String temThree = "grep '拍照指令' | grep '测0000'";
        String temFour = "grep '0000000000000000' | egrep '【9500|【1500'";

        // 构建返回结果 - 替换车牌和编码值
        String result = tem.replace("测0000", msg).replace("0000000000000000", encodeMsg);
        String resultOne = temOne.replace("0000000000000000", encodeMsg);
        String resultTwo = temTwo.replace("0000000000000000", encodeMsg);
        String resultThree = temThree.replace("测0000", msg);
        String resultFour = temFour.replace("0000000000000000", encodeMsg);
        String resultFive = temFive.replace("0000000000000000", encodeMsg);

        resultMap.put("GritLog全量日志", result);
        resultMap.put("GritLog指令日志", resultFive);
        resultMap.put("Linux指令下发9502", resultTwo);
        resultMap.put("Linux报文返回1502", resultOne);
        resultMap.put("Linux拍照常规日志", resultThree);
        resultMap.put("Linux拍照全量日志", resultFour);
        return resultMap;
    }

}