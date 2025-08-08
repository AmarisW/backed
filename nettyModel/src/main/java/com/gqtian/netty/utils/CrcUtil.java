package com.gqtian.netty.utils;

/**
 * 🔐 CRC校验工具类
 * <p>
 * 提供CRC16校验码计算功能，用于数据完整性校验
 * </p>
 *
 * @author gqtian
 * @version 1.0
 **/
public class CrcUtil {
    
    /**
     * CRC16校验算法
     * <p>
     * 使用CRC16算法计算字节数组的校验码
     * </p>
     *
     * @param bytes 待校验的字节数组
     * @return int CRC16校验码
     */
    public static int crc16(byte[] bytes) {
        int crc = 0xFFFF;
        for (byte aByte : bytes) {
            crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
            crc ^= (aByte & 0xff);// byte to int, trunc sign
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }
        crc &= 0xffff;
        return crc;
    }
    
    /**
     * 获取CRC校验码字节数组
     * <p>
     * 计算字节数组的CRC16校验码，并将其转换为2字节的字节数组
     * </p>
     *
     * @param bytes 待校验的字节数组
     * @return byte[] 2字节的CRC校验码数组
     */
    public static byte[] crcBytes(byte[] bytes) {
        byte[] int2bytes = ByteArrayUtil.int2bytes(crc16(bytes));
        return new byte[]
        {int2bytes[2], int2bytes[3]};
    }
    
}