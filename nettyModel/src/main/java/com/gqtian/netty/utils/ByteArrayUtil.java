package com.gqtian.netty.utils;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 🔧 字节数组工具类
 * <p>
 * 提供各种字节数组操作方法，包括类型转换、编码解码、字符串处理等功能
 * </p>
 *
 * @author gqtian
 * @version 1.0
 * @date 2018-12-27 17:27
 **/
public class ByteArrayUtil {

    /**
     * byte数组拼接
     * <p>
     * 将两个字节数组合并成一个数组
     * </p>
     *
     * @param first 第一个字节数组
     * @param back 第二个字节数组
     * @return byte[] 拼接后的字节数组，如果任一数组为null则返回null
     */
    public static byte[] append(byte[] first, byte[] back) {
        if (null == first || null == back) {
            return null;
        }
        int length = first.length + back.length;
        byte[] res = new byte[length];
        System.arraycopy(first, 0, res, 0, first.length);
        System.arraycopy(back, 0, res, first.length, back.length);
        return res;

    }

    /**
     * 截取字节数组
     * <p>
     * 从指定位置开始截取指定长度的字节数组
     * </p>
     *
     * @param data 源字节数组
     * @param off 起始位置
     * @param length 截取长度
     * @return byte[] 截取后的字节数组
     */
    public static byte[] subBytes(byte[] data, int off, int length) {
        byte[] res = new byte[length];
        System.arraycopy(data, off, res, 0, length);
        return res;
    }

    /**
     * short转换为byte[]
     * <p>
     * 将short类型数值转换为2字节的字节数组（大端序）
     * </p>
     *
     * @param number short数值
     * @return byte[] 2字节的字节数组
     */
    public static byte[] short2Bytes(short number) {
        byte[] b = new byte[2];
        b[0] = (byte)(number >> 8);
        b[1] = (byte)(number & 0xFF);
        return b;
    }

    /**
     * byte[]转换为short
     * <p>
     * 将2字节的字节数组转换为short类型数值（大端序）
     * </p>
     *
     * @param bytes 2字节的字节数组
     * @return short short数值
     */
    public static short bytes2Short(byte[] bytes) {
        short z = (short)((bytes[0] << 8) | (bytes[1] & 0xFF));
        return z;
    }

    /**
     * byte数组转换为int
     * <p>
     * 将字节数组转换为int类型数值（大端序）
     * </p>
     *
     * @param data 字节数组
     * @return int int数值
     */
    public static int bytes2int(byte[] data) {
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < data.length; i++) {
            n <<= 8;
            temp = data[i] & mask;
            n |= temp;
        }
        return n;
    }

    /**
     * int转换为byte数组
     * <p>
     * 将int类型数值转换为4字节的字节数组（大端序）
     * </p>
     *
     * @param n int数值
     * @return byte[] 4字节的字节数组
     */
    public static byte[] int2bytes(int n) {
        byte[] b = new byte[4];

        for (int i = 0; i < 4; i++) {
            b[i] = (byte)(n >> (24 - i * 8));

        }
        return b;
    }

    /**
     * byte[] 转换为 GBK 编码的 字符串
     * <p>
     * 将字节数组按GBK编码转换为字符串
     * </p>
     *
     * @param byteArray 字节数组
     * @return java.lang.String GBK编码的字符串
     */
    public static String bytes2gbkString(byte[] byteArray) {
        return bytes2string(byteArray, "GBK");
    }

    /**
     * byte[] 转换为 string
     * <p>
     * 将字节数组按指定编码转换为字符串
     * </p>
     *
     * @param byteArray 字节数组
     * @param charset 字符编码
     * @return java.lang.String 指定编码的字符串
     */
    public static String bytes2string(byte[] byteArray, String charset) {
        if (byteArray.length == 0) {
            return "";
        }
        return new String(byteArray, Charset.forName(charset));
    }

    /**
     * byte[] 转换为 string
     * <p>
     * 将字节数组按默认编码转换为字符串
     * </p>
     *
     * @param byteArray 字节数组
     * @return java.lang.String 默认编码的字符串
     */
    public static String bytes2string(byte[] byteArray) {
        if (byteArray.length == 0) {
            return "";
        }
        return new String(byteArray, Charset.defaultCharset());
    }

    /**
     * byte[] 转换为 float
     * <p>
     * 将字节数组转换为float类型数值
     * </p>
     *
     * @param floatArray 字节数组
     * @return float float数值
     */
    public static float bytes2float(byte[] floatArray) {
        int accum = 0;
        for (int i = 0, j = 0; i < floatArray.length; i++) {
            accum = accum | (floatArray[i] & 0xff) << j;
            j += 8;
        }
        return Float.intBitsToFloat(accum);
    }

    /**
     * byte[] 转换为 double
     * <p>
     * 将字节数组转换为double类型数值
     * </p>
     *
     * @param doubleArray 字节数组
     * @return double double数值
     */
    public static double bytes2double(byte[] doubleArray) {
        long value = 0;
        for (int i = 0; i < doubleArray.length; i++) {
            value |= ((long)(doubleArray[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }

    /**
     * 数组转换成十六进制字符串
     * <p>
     * 将字节数组转换为大写十六进制字符串表示
     * </p>
     *
     * @param array 字节数组
     * @return java.lang.String 十六进制字符串
     */
    public static String bytes2HexStr(byte[] array) {
        StringBuilder sb = new StringBuilder(array.length);
        String sTemp;
        for (byte b : array) {
            sTemp = Integer.toHexString(0xFF & b);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 数组转换成带0x的十六进制字符串
     * <p>
     * 将字节数组转换为带"0x"前缀的十六进制字符串表示
     * </p>
     *
     * @param array 字节数组
     * @return java.lang.String 带"0x"前缀的十六进制字符串
     */
    public static String bytes2FullHexStr(byte[] array) {
        StringBuffer sb = new StringBuffer(array.length);
        sb.append("0x");
        String sTemp;
        for (int i = 0; i < array.length; i++) {
            sTemp = Integer.toHexString(0xFF & array[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp);
            if (i < array.length - 1) {
                sb.append("0x");
            }
        }
        return sb.toString().toLowerCase();
    }

    /**
     * short 转换为十六进制字符串
     * <p>
     * 将short类型数值转换为十六进制字符串表示
     * </p>
     *
     * @param num short数值
     * @return java.lang.String 十六进制字符串
     */
    public static String short2HexStr(short num) {
        return Integer.toHexString(num);
    }

    /**
     * 把16进制字符串转换成字节数组
     * <p>
     * 将十六进制字符串转换为字节数组
     * </p>
     *
     * @param hex 十六进制字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStr2Bytes(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte)(toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    /**
     * 把带0x的16进制字符串转换成字节数组
     * <p>
     * 将带"0x"前缀的十六进制字符串转换为字节数组
     * </p>
     *
     * @param hex 带"0x"前缀的十六进制字符串
     * @return byte[] 字节数组
     */
    public static byte[] fullHexStr2Bytes(String hex) {
        hex = hex.toLowerCase().replaceAll("0x", "").trim().toUpperCase();
        return hexStr2Bytes(hex);
    }

    /**
     * 将字符转换为对应的十六进制数值
     * <p>
     * 将十六进制字符('0'-'F')转换为对应的数值(0-15)
     * </p>
     *
     * @param c 十六进制字符
     * @return int 对应的数值
     */
    private static int toByte(char c) {
        byte b = (byte)"0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * byte数组转时间字符串 格式 yyMMddHHmmss
     * <p>
     * 将字节数组转换为时间字符串，格式为yyMMddHHmmss
     * </p>
     *
     * @param array 字节数组
     * @return java.lang.String 时间字符串
     */
    public static String bytes2timeStr(byte[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : array) {
            int timeUnit = byte2UnsignedInt(b);
            if (timeUnit < 10) {
                stringBuilder.append(0);
            }
            stringBuilder.append(timeUnit);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date()).substring(0, 2) + stringBuilder;
    }

    /**
     * byte转无符号整数
     * <p>
     * 将byte类型数值转换为无符号整数(0-255)
     * </p>
     *
     * @param value byte数值
     * @return int 无符号整数
     */
    public static int byte2UnsignedInt(byte value) {
        return Byte.toUnsignedInt(value);
    }

    /**
     * 将一个long转换成8位的byte[]
     * <p>
     * 将long类型数值转换为8字节的字节数组（大端序）
     * </p>
     *
     * @param num long值
     * @return 长度是8的byte[] 8字节的字节数组
     */
    public static byte[] longToBytes(long num) {
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            b[i] = (byte)(num >>> (56 - i * 8));
        }
        return b;
    }

    /**
     * 将一个数组转换成一个long值
     * <p>
     * 将8字节的字节数组转换为long类型数值（大端序）
     * </p>
     *
     * @param b 长度是8的byte[] 8字节的字节数组
     * @return long值 long类型数值
     */
    public static long bytesToLong(byte[] b) {
        int mask = 0xff;
        long temp = 0;
        long res = 0;
        for (int i = 0; i < 8; i++) {
            res <<= 8;
            temp = b[i] & mask;
            res |= temp;
        }
        return res;
    }

    /**
     * 将一个byte数组转换成二进制字符串
     * <p>
     * 将字节数组转换为二进制字符串表示
     * </p>
     *
     * @param bytes 字节数组
     * @return java.lang.String 二进制字符串
     */
    public static String bytes2bitStr(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(byte2bitStr(b));
        }
        return stringBuilder.toString();
    }

    /**
     * 将一个byte转换成二进制字符串
     * <p>
     * 将单个字节转换为8位二进制字符串表示
     * </p>
     *
     * @param b 字节
     * @return java.lang.String 8位二进制字符串
     */
    public static String byte2bitStr(byte b) {
        return "" + (byte)((b >> 7) & 0x1) + (byte)((b >> 6) & 0x1) + (byte)((b >> 5) & 0x1) + (byte)((b >> 4) & 0x1)
            + (byte)((b >> 3) & 0x1) + (byte)((b >> 2) & 0x1) + (byte)((b >> 1) & 0x1) + (byte)((b >> 0) & 0x1);
    }

}