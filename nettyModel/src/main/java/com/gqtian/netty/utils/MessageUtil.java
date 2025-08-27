package com.gqtian.netty.utils;

import java.util.Random;

import org.springframework.util.StringUtils;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.CharsetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

/**
 * 🔔 消息处理工具类
 * <p>
 * 提供Netty通信中常用的消息处理方法，包括字符串编解码、数据转义、校验码计算等功能
 * </p>
 * 
 * @author GQTIAN
 * @version 1.0
 * @date 2025/8/5 09:34
 **/
@Slf4j
public class MessageUtil {

    private static final Random RANDOM = new Random();

    /**
     * BCC校验(异或校验)
     * <p>
     * 对ByteBuf中的所有字节进行异或运算，得到校验码
     * </p>
     *
     * @param byteBuf 待校验的数据缓冲区
     * @return byte 校验码
     */
    public static byte bcc(ByteBuf byteBuf) {
        byte cs = 0;
        while (byteBuf.isReadable()) {
            cs ^= byteBuf.readByte();
        }
        byteBuf.resetReaderIndex();
        return cs;
    }

    /**
     * 获取一个消息号
     * <p>
     * 生成一个0-65535之间的随机整数作为消息序列号
     * </p>
     *
     * @return int 消息序列号
     */
    public static synchronized int randomSeriesNum() {
        return RANDOM.nextInt(0xffff);
    }

    /**
     * 解码ByteBuf为字符串并忽略填充的零字节
     * <p>
     * 将ByteBuf中的数据解码为UTF-8字符串，并移除值为0的字节
     * </p>
     *
     * @param byteBuf 源数据缓冲区
     * @return java.lang.String 解码后的字符串
     */
    public static String decodeToStrIgnoreFillZero(ByteBuf byteBuf) {
        return decodeToStr(byteBuf, (byte)0);
    }

    /**
     * <p>
     * 去除GBK编码字符串前后的指定byte字符,byte字符会转换成GBK编码
     * </p>
     *
     * @param byteBuf 源数据
     * @param prefixOrSuffix 目标字符,会转换成GBK编码
     * @return java.lang.String
     */
    public static String decodeToStr(ByteBuf byteBuf, byte prefixOrSuffix) {
        byte[] bytes = ByteBufUtil.getBytes(byteBuf);
        byteBuf.release();
        String pos = new String(new byte[] {prefixOrSuffix}, CharsetUtil.CHARSET_UTF_8);
        return new String(bytes, CharsetUtil.CHARSET_UTF_8).replaceAll(pos, "");
    }

    /**
     * 解码ByteBuf为UTF-8字符串
     * <p>
     * 将ByteBuf中的字节数据直接转换为UTF-8编码的字符串
     * </p>
     *
     * @param byteBuf 源数据缓冲区
     * @return java.lang.String 解码后的字符串
     */
    public static String decodeToStr(ByteBuf byteBuf) {
        byte[] bytes = ByteBufUtil.getBytes(byteBuf);
        return new String(bytes, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 左侧补齐
     *
     * @param bytes 原始字节数组
     * @param length 总共的长度
     * @param padByte 填补的字节
     * @return byte[] 补齐后的字节数组
     */
    public static byte[] leftPad(byte[] bytes, int length, byte padByte) {
        int i = length - bytes.length;
        if (i <= 0) {
            return bytes;
        }
        byte[] result = new byte[length];
        System.arraycopy(bytes, 0, result, i, bytes.length);
        for (int j = 0; j < i; j++) {
            result[j] = padByte;
        }
        return result;
    }

    /**
     * 生成32位UUID
     * <p>
     * 生成一个不包含"-"的32位UUID字符串
     * </p>
     *
     * @return java.lang.String 32位UUID字符串
     */
    public static String ranDomId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * <p>
     * jt809转义
     * </p>
     *
     * @param source 源数据
     * @param isEncode 标志是编码还是解码
     * @return io.netty.buffer.ByteBuf
     */
    public static ByteBuf escape(ByteBuf source, boolean isEncode) {
        int length = source.readableBytes();
        // 除去头尾的字节内容
        byte[] messageBytes = new byte[length];
        source.getBytes(0, messageBytes).release();
        ByteBuf result = Unpooled.buffer();
        if (isEncode) {
            encodeEscape(messageBytes, result);
        } else {
            decodeEscape(messageBytes, result);
        }
        return result;
    }

    /**
     * <p>
     * 解密时的数据转义,数据接收需要先解密再转义
     * </p>
     *
     * @param data 字节内容
     * @param result 转义后的结果缓冲区
     */
    private static void decodeEscape(byte[] data, ByteBuf result) {
        // 对特殊字符进行转换，转换后的存入缓存
        for (int i = 0; i < data.length; i++) {
            byte indexByte = data[i];
            if (indexByte != 0x5A && indexByte != 0x5E) {
                result.writeByte(indexByte);
                continue;
            }
            if (i == data.length - 1) {
                continue;
            }
            byte nextByte = data[i + 1];
            if ((indexByte == 0x5A)) {
                if (nextByte == 0x01) {
                    result.writeByte(0x5B);
                }
                if (nextByte == 0x02) {
                    result.writeByte(0x5A);
                }
            }

            if ((indexByte == 0x5E)) {
                if (nextByte == 0x01) {
                    result.writeByte(0x5D);
                }
                if (nextByte == 0x02) {
                    result.writeByte(0x5E);
                }
            }
            i++;
        }
    }

    /**
     * <p>
     * 加密时的数据体转义,需要先转义再加密
     * </p>
     *
     * @param data 字节内容
     * @param result 转义后的结果缓冲区
     */
    private static void encodeEscape(byte[] data, ByteBuf result) {
        for (byte indexByte : data) {
            if (indexByte == 0x5B) {
                result.writeByte(0x5A);
                result.writeByte(0x01);
                continue;
            }
            if (indexByte == 0x5A) {
                result.writeByte(0x5A);
                result.writeByte(0x02);
                continue;
            }
            if (indexByte == 0x5D) {
                result.writeByte(0x5E);
                result.writeByte(0x01);
                continue;
            }

            if (indexByte == 0x5E) {
                result.writeByte(0x5E);
                result.writeByte(0x02);
                continue;
            }
            result.writeByte(indexByte);
        }

    }

    /**
     * 按指定长度写入字符串，不足部分补零
     * <p>
     * 将字符串按GBK编码写入ByteBuf，如果长度不足指定长度，则在末尾补零
     * </p>
     *
     * @param source 源字符串
     * @param byteBuf 目标缓冲区
     * @param length 指定长度
     */
    public static void writeStringByZeroByteInLength(String source, ByteBuf byteBuf, int length) {
        if (StringUtils.isEmpty(source)) {
            source = "";
        }
        byte[] sourceBytes = source.getBytes(CharsetUtil.CHARSET_GBK);
        byteBuf.writeBytes(sourceBytes);
        if (sourceBytes.length < length) {
            int residue = length - sourceBytes.length;
            byteBuf.writeZero(residue);
        }
    }

    /**
     * 解析时删除填充的0
     *
     * @param byteBuf 数据缓冲区
     * @param length 读取长度
     * @return java.lang.String 解析后的字符串
     */
    public static String removeStringByZero(ByteBuf byteBuf, int length) {
        if (StringUtils.isEmpty(byteBuf)) {
            return null;
        }
        return byteBuf.readCharSequence(length, CharsetUtil.CHARSET_UTF_8).toString().trim();
    }

    /**
     * 字符串转成16进制
     */
    public static String encodeMsg(String msg) {
        byte[] sourceBytes = msg.getBytes(CharsetUtil.CHARSET_GBK);
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(sourceBytes);
        ByteBuf escapedBuf = MessageUtil.escape(buf, true);
        return ByteArrayUtil.bytes2HexStr(ByteBufUtil.getBytes(escapedBuf));
    }

    /**
     * 十六进制字符串消息解码为原始字符串
     * 
     * @param msg 十六进制字符串消息
     * @return java.lang.String 解码后的原始字符串
     */
    public static String decodeMsg(String msg) {
        // 将16进制转化成字符串
        byte[] bytes = ByteArrayUtil.hexStr2Bytes(msg);
        ByteBuf byteBuf = Unpooled.copiedBuffer(bytes);
        ByteBuf unescapedBuf = MessageUtil.escape(byteBuf, false);
        byte[] resultBytes = ByteBufUtil.getBytes(unescapedBuf);
        return new String(resultBytes, CharsetUtil.CHARSET_GBK).trim();
    }

}