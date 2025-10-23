/** Copyright (c) 2020 linkcld.com to Present. All rights reserved. */
package com.gqtian.hbase.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class UsbGpsData {

    /** 名称 */
    private String name;

    /** 车牌号 */
    private String plateNo;

    /** 车牌颜色 */
    private Integer plateColor;

    /** 定位时间 */
    private Date posTime;

    /** 定位时间 */
    private Date posDate;

    /** 原始经度 */
    private Double longitude;

    /** 纬度 */
    private Double latitude;

    /** GCJ纬度 */
    private Double new_latitude;

    /** GCJ经度 */
    private Double new_longitude;

    /** GPS速度 */
    private Integer gpsSpeed;

    /** 行车记录仪速度 */
    private Integer recSpeed;

    /** 总里程 */
    private Long totalMileage;

    /** 方向 */
    private Integer direction;

    /** 海拔 */
    private Integer altitude;

    /** 车辆状态，（空重车状态，0:空车;1:重车;2:掉线） */
    private Integer vehicleState = 2;

    /** 车辆在线状态，0:离线;1:在线） */
    private Integer lineState = 1;

    /** 报警状态 */
    private Long alarmState;

    /** 设备号 */
    private String deviceNo;

    /** 是否纠偏 */
    private Integer posEncrypt;

    /** 行业类型 */
    private String industry;

    /** 车辆所属市行政区划 */
    private String scode;

    /** 车辆所属县行政区划 */
    private String xcode;

    /** 接收时间 */
    private Date receiveTime;

    /** 运营商编码 */
    private Long platformCode;

    /** 运营商名称 */
    private String platformName;

    /** 载客类型 */
    private Integer carryType;

    /** 车辆状态 */
    private Long state;

    /** 点火状态 */
    private Integer accState;

    /** 超速信息 */
    private Integer overSpeed;

    /** 疲劳驾驶 */
    private Integer fatigueDriving;

    /** 运营状态 */
    private Integer operation;

    /** 是否补发 */
    private Integer isReissue = 0;

    /** 类型 */
    private String type;

    /** 企业名称 */
    private String companyName;

    /** 企业负责人 */
    private String responsiblePerson;

    /** 企业负责人电话 */
    private String responsiblePersonTel;

    /** 货物名称 */
    private String goodsName;

    /** 目的地 */
    private String unloadPlace;

    /** （车辆运行状态，0：5分钟以内，1：5-10分钟以内，2:10-30分钟以内，3:30分钟以上） */
    private Integer runStatus = -1;

    @JsonIgnore
    public String getKey() {
        return this.plateNo.trim() + "_" + this.plateColor;
    }

    public long getPosTime() {
        return posTime.getTime();
    }

    public long getPosDate() {
        return posTime.getTime();
    }
}
