/** Copyright (c) 2020 linkcld.com to Present. All rights reserved. */
package com.gqtian.hbase.entity;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson2.annotation.JSONField;

import lombok.Data;

@Data
public class GpsData implements Serializable {

    /** ID */
    private String id;

    /** 名称 */
    private String name;

    /** 车牌号 */
    @JSONField(name = "pn")
    private String plateNumber;

    /** 车牌颜色 */
    @JSONField(name = "pc")
    private Integer plateColor;

    /** 运营商编码 */
    @JSONField(name = "pco")
    private Long platformCode;

    /** 终端定位时间 */
    @JSONField(name = "tt")
    private Date terminalTime;

    /** 原始经度 */
    @JSONField(name = "olon")
    private Double originalLongitude;

    /** 原始纬度 */
    @JSONField(name = "olat")
    private Double originalLatitude;

    /** 经度 */
    @JSONField(name = "lon")
    private Double longitude;

    /** 纬度 */
    @JSONField(name = "lat")
    private Double latitude;

    /** GPS速度 */
    @JSONField(name = "gs")
    private Double gpsSpeed;

    /** 行车记录仪速度 */
    @JSONField(name = "spe")
    private Double speed;

    /** 里程 */
    @JSONField(name = "mil")
    private Double mileage;

    /** 海拔 */
    @JSONField(name = "alt")
    private Integer altitude;

    /** 方向 */
    @JSONField(name = "dir")
    private Integer direction;

    /** 数据接收时间 */
    @JSONField(name = "rt")
    private Date receiveTime;

    /** 是否补发0否 1是 */
    @JSONField(name = "rei")
    private Integer reissue = 0;

    /** 状态，报文原始值 【子数据按位取值】 */
    @JSONField(name = "sta")
    private Long status;

    /** 点火状态 */
    private Integer acc;

    /** 0：未定位； 1：定位 */
    @JSONField(name = "loc")
    private Integer location;

    /** 货车 0：空车; 1：半载; 10：保留; 11：满载 客车 0：空车; 1：重车 */
    @JSONField(name = "loa")
    private Integer loading;

    /** 0：运营状态； 1：停运状态 */
    @JSONField(name = "ope")
    private Integer operation;

    /** 报警标志，报文原始值 */
    @JSONField(name = "ala")
    private Long alarm;

    /** 疲劳驾驶原始值 */
    @JSONField(name = "fd")
    private Integer fatigueDriving;

    /** 危险驾驶 */
    @JSONField(name = "ew")
    private Integer earlyWarning;

    /** 超速状态 */
    @JSONField(name = "os")
    private Integer overSpeed;

    /** 创建时间 */
    @JSONField(name = "ct")
    private Date createTime;

    /** 行业类型code */
    @JSONField(name = "ind")
    private String industry;

    /** 行政区划代码 */
    @JSONField(name = "ac")
    private String areaCode;

    /** 1：车；2：船 */
    @JSONField(name = "cat")
    private Integer category = 1;

    /** 信号灯状态 */
    @JSONField(name = "ls")
    private Long lightStatus;

    /** 设备号 */
    @JSONField(name = "dn")
    private String deviceNumber;
}
