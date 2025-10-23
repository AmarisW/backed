package com.gqtian.hbase.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author Zhangjy
 * @Date 2020/11/24 21:13
 * @Desc
 */
public class GpsHBaseTableConst {

    public static Charset defaultCharset = StandardCharsets.UTF_8;

    public static int ROW_KEY_PLATE_NO_BYTES = 11;

    /**
     * 列簇
     */
    public static byte[] COLUMN_FAMILY = "cf".getBytes(defaultCharset);

    /**
     * 名称
     */
    public static byte[] COLUMN_NAME = "name".getBytes(defaultCharset);

    /**
     * 车牌号
     */
    public static byte[] COLUMN_PLATE_NUMBER = "pn".getBytes(defaultCharset);

    /**
     * 车牌颜色
     */
    public static byte[] COLUMN_PLATE_COLOR = "pc".getBytes(defaultCharset);

    /**
     * 运营商编码
     */
    public static byte[] COLUMN_PLATFORM_CODE = "pco".getBytes(defaultCharset);

    /**
     * 终端定位时间
     */
    public static byte[] COLUMN_TERMINAL_TIME = "tt".getBytes(defaultCharset);

    /**
     * 原始经度
     */
    public static byte[] COLUMN_ORIGINAL_LONGITUDE = "olon".getBytes(defaultCharset);

    /**
     * 原始纬度
     */
    public static byte[] COLUMN_ORIGINAL_LATITUDE = "olat".getBytes(defaultCharset);

    /**
     * 经度
     */
    public static byte[] COLUMN_LONGITUDE = "lon".getBytes(defaultCharset);

    /**
     * 纬度
     */
    public static byte[] COLUMN_LATITUDE = "lat".getBytes(defaultCharset);

    /**
     * 车辆速度
     */
    public static byte[] COLUMN_GPS_SPEED = "gs".getBytes(defaultCharset);

    /**
     * 行车记录仪速度
     */
    public static byte[] COLUMN_SPEED = "spe".getBytes(defaultCharset);

    /**
     * 行车记录仪累计里程
     */
    public static byte[] COLUMN_MILEAGE = "mil".getBytes(defaultCharset);

    /**
     * 方向
     */
    public static byte[] COLUMN_DIRECTION = "dir".getBytes(defaultCharset);

    /**
     * 高程
     */
    public static byte[] COLUMN_ALTITUDE = "alt".getBytes(defaultCharset);

    /**
     * GPS接收时间
     */
    public static byte[] COLUMN_RECEIVE_TIME = "rt".getBytes(defaultCharset);

    /**
     * 是否补报
     */
    public static byte[] COLUMN_REISSUE = "rei".getBytes(defaultCharset);

    /**
     * 状态
     */
    public static byte[] COLUMN_STATUS = "sta".getBytes(defaultCharset);

    /**
     * 报警
     */
    public static byte[] COLUMN_ALARM = "ala".getBytes(defaultCharset);

    /**
     * 点火状态
     */
    public static byte[] COLUMN_ACC_STATE = "acc".getBytes(defaultCharset);

    /**
     * 超速状态
     */
    public static byte[] COLUMN_OVER_SPEED = "os".getBytes(defaultCharset);

    /**
     * 疲劳驾驶
     */
    public static byte[] COLUMN_FATIGUE_DRIVING = "fd".getBytes(defaultCharset);

    /**
     * 行业类型
     */
    public static byte[] COLUMN_INDUSTRY = "ind".getBytes(defaultCharset);
}
