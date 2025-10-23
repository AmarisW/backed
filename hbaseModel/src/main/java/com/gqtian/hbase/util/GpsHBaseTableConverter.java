/** Copyright (c) 2020 linkcld.com to Present. All rights reserved. */
package com.gqtian.hbase.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.util.Assert;

import com.gqtian.hbase.constant.GpsHBaseTableConst;
import com.gqtian.hbase.entity.GpsData;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author Zhangjy @Date 2020/11/24 21:13 @Desc
 */
@Slf4j
public class GpsHBaseTableConverter {

    /** 获取rowkey */
    public static byte[] getRowKey(GpsData gpsData) {
        return getRowKey(gpsData.getPlateNumber(), gpsData.getPlateColor(), gpsData.getTerminalTime());
    }

    /** 获取rowkey */
    public static byte[] getRowKey(String plateNumber, Integer plateColor, Date terminalTime) {
        Assert.hasText(plateNumber, plateNumber);
        Assert.notNull(plateColor, "plateColor can't be null");
        Assert.notNull(terminalTime, "terminalTime can't be null");
        byte[] plateNoBytes = StringUtils.reverse(plateNumber).getBytes(GpsHBaseTableConst.defaultCharset);
        if (plateNoBytes.length < GpsHBaseTableConst.ROW_KEY_PLATE_NO_BYTES) {
            plateNoBytes = Bytes.padTail(plateNoBytes, GpsHBaseTableConst.ROW_KEY_PLATE_NO_BYTES - plateNoBytes.length);
        }
        plateNoBytes = Bytes.add(plateNoBytes, new byte[] {plateColor.byteValue()});
        return Bytes.add(plateNoBytes, Bytes.toBytes(terminalTime.getTime()));
    }

    /**
     * 根据返回数据组装返回gps数据
     *
     * @param rs
     * @return
     */
    public static List<GpsData> getResultList(ResultScanner rs) {
        List<GpsData> list = new ArrayList<>(300);
        for (Result r : rs) {
            list.add(getResult(r));
        }
        return list;
    }

    /**
     * 根据返回数据组装返回gps数据
     *
     * @param result hbase result
     * @return gps data
     */
    public static GpsData getResult(Result result) {
        GpsData gpsData = new GpsData();
        byte[] columnName = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_NAME);
        if (!ArrayUtil.isEmpty(columnName)) {
            gpsData.setName(Bytes.toString(columnName));
        }
        byte[] columnPlateNumber =
            result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_PLATE_NUMBER);
        if (!ArrayUtil.isEmpty(columnPlateNumber)) {
            gpsData.setPlateNumber(Bytes.toString(columnPlateNumber));
        }
        byte[] columnPlateColor =
            result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_PLATE_COLOR);
        if (!ArrayUtil.isEmpty(columnPlateColor)) {
            gpsData.setPlateColor(Bytes.toInt(columnPlateColor));
        }
        byte[] columnTerminalTime =
            result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_TERMINAL_TIME);
        if (!ArrayUtil.isEmpty(columnTerminalTime)) {
            gpsData.setTerminalTime(bytesToDate(columnTerminalTime));
        }
        byte[] columnReissue = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_REISSUE);
        if (!ArrayUtil.isEmpty(columnReissue)) {
            gpsData.setReissue(Bytes.toInt(columnReissue));
        }
        byte[] columnPlatformCode =
            result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_PLATFORM_CODE);
        if (!ArrayUtil.isEmpty(columnPlatformCode)) {
            gpsData.setPlatformCode(Bytes.toLong(columnPlatformCode));
        }
        byte[] columnGpsSpeed = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_GPS_SPEED);
        if (!ArrayUtil.isEmpty(columnGpsSpeed)) {
            gpsData.setGpsSpeed(Bytes.toDouble(columnGpsSpeed));
        }
        byte[] columnOriginalLongitude =
            result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_ORIGINAL_LONGITUDE);
        if (!ArrayUtil.isEmpty(columnOriginalLongitude)) {
            gpsData.setOriginalLongitude(Bytes.toDouble(columnOriginalLongitude));
        }
        byte[] columnOriginalLatitude =
            result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_ORIGINAL_LATITUDE);
        if (!ArrayUtil.isEmpty(columnOriginalLatitude)) {
            gpsData.setOriginalLatitude(Bytes.toDouble(columnOriginalLatitude));
        }
        byte[] columnLongitude = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_LONGITUDE);
        if (!ArrayUtil.isEmpty(columnLongitude)) {
            gpsData.setLongitude(Bytes.toDouble(columnLongitude));
        }
        byte[] columnLatitude = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_LATITUDE);
        if (!ArrayUtil.isEmpty(columnLatitude)) {
            gpsData.setLatitude(Bytes.toDouble(columnLatitude));
        }
        byte[] columnAltitude = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_ALTITUDE);
        if (!ArrayUtil.isEmpty(columnAltitude)) {
            gpsData.setAltitude(Bytes.toInt(columnAltitude));
        }
        byte[] columnAccState = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_ACC_STATE);
        if (!ArrayUtil.isEmpty(columnAccState)) {
            gpsData.setAcc(Bytes.toInt(columnAccState));
        }
        byte[] columnDirection = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_DIRECTION);
        if (!ArrayUtil.isEmpty(columnDirection)) {
            gpsData.setDirection(Bytes.toInt(columnDirection));
        }
        byte[] columnSpeed = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_SPEED);
        if (!ArrayUtil.isEmpty(columnSpeed)) {
            gpsData.setSpeed(Bytes.toDouble(columnSpeed));
        }
        byte[] columnMileage = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_MILEAGE);
        if (!ArrayUtil.isEmpty(columnMileage)) {
            gpsData.setMileage(Bytes.toDouble(columnMileage));
        }
        byte[] columnStatus = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_STATUS);
        if (!ArrayUtil.isEmpty(columnStatus)) {
            gpsData.setStatus(Bytes.toLong(columnStatus));
        }
        byte[] columnAlarm = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_ALARM);
        if (!ArrayUtil.isEmpty(columnAlarm)) {
            gpsData.setAlarm(Bytes.toLong(columnAlarm));
        }
        byte[] columnReceiveTime =
            result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_RECEIVE_TIME);
        if (!ArrayUtil.isEmpty(columnReceiveTime)) {
            gpsData.setReceiveTime(bytesToDate(columnReceiveTime));
        }
        byte[] columnIndustry = result.getValue(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_INDUSTRY);
        if (!ArrayUtil.isEmpty(columnIndustry)) {
            gpsData.setIndustry(Bytes.toString(columnIndustry));
        }
        return gpsData;
    }

    // /**
    // * 根据返回数据组装返回gps数据
    // *
    // * @param result hbase result
    // * @return gps data
    // */
    // public static GpsData getResult(Result result) {
    // GpsData gpsData = new GpsData();
    // gpsData.setPlateNumber(
    // familyValueConverter.getString(result, GpsHBaseTableConst.COLUMN_PLATE_NUMBER));
    // gpsData.setPlateColor(
    // familyValueConverter.getInt(result, GpsHBaseTableConst.COLUMN_PLATE_COLOR));
    // gpsData.setTerminalTime(
    // familyValueConverter.getDate(result, GpsHBaseTableConst.COLUMN_TERMINAL_TIME));
    // gpsData.setReissue(familyValueConverter.getInt(result, GpsHBaseTableConst.COLUMN_REISSUE));
    // gpsData.setPlatformCode(
    // familyValueConverter.getLong(result, GpsHBaseTableConst.COLUMN_PLATFORM_CODE));
    // gpsData.setGpsSpeed(
    // familyValueConverter.getDouble(result, GpsHBaseTableConst.COLUMN_GPS_SPEED));
    // gpsData.setLongitude(
    // familyValueConverter.getDouble(result, GpsHBaseTableConst.COLUMN_ORIGINAL_LONGITUDE));
    // gpsData.setLatitude(
    // familyValueConverter.getDouble(result, GpsHBaseTableConst.COLUMN_ORIGINAL_LATITUDE));
    // gpsData.setLongitude(
    // familyValueConverter.getDouble(result, GpsHBaseTableConst.COLUMN_LONGITUDE));
    // gpsData.setLatitude(familyValueConverter.getDouble(result, GpsHBaseTableConst.COLUMN_LATITUDE));
    // gpsData.setAltitude(familyValueConverter.getInt(result, GpsHBaseTableConst.COLUMN_ALTITUDE));
    // gpsData.setDirection(familyValueConverter.getInt(result, GpsHBaseTableConst.COLUMN_DIRECTION));
    // gpsData.setSpeed(
    // familyValueConverter.getDouble(result, GpsHBaseTableConst.COLUMN_SPEED));
    // gpsData.setMileage(
    // familyValueConverter.getDouble(result, GpsHBaseTableConst.COLUMN_MILEAGE));
    // gpsData.setStatus(familyValueConverter.getLong(result, GpsHBaseTableConst.COLUMN_STATUS));
    // gpsData.setAlarm(familyValueConverter.getLong(result, GpsHBaseTableConst.COLUMN_ALARM));
    // gpsData.setReceiveTime(
    // familyValueConverter.getDate(result, GpsHBaseTableConst.COLUMN_RECEIVE_TIME));
    // return gpsData;
    // }

    private static String bytesToString(byte[] value) {
        if (ArrayUtil.isNotEmpty(value)) {
            return Bytes.toString(value);
        } else {
            return null;
        }
    }

    private static Integer bytesToInt(byte[] value) {
        if (ArrayUtil.isNotEmpty(value)) {
            return Bytes.toInt(value);
        } else {
            return null;
        }
    }

    private static Long bytesToLong(byte[] value) {
        if (ArrayUtil.isNotEmpty(value)) {
            return Bytes.toLong(value);
        } else {
            return null;
        }
    }

    private static Double bytesToDouble(byte[] value) {
        if (ArrayUtil.isNotEmpty(value)) {
            return Bytes.toDouble(value);
        } else {
            return null;
        }
    }

    /**
     * 根据Gps参数获取数据
     *
     * @param gpsDataList
     */
    public static List<Put> getRow(List<GpsData> gpsDataList) {
        return gpsDataList.parallelStream().map(GpsHBaseTableConverter::getRow).collect(Collectors.toList());
    }

    /**
     * 根据Gps参数获取单行数据
     *
     * @param gpsData
     */
    public static Put getRow(GpsData gpsData) {
        Put row = new Put(getRowKey(gpsData));
        row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_PLATE_NUMBER,
            Bytes.toBytes(gpsData.getPlateNumber()));
        row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_PLATE_COLOR,
            Bytes.toBytes(gpsData.getPlateColor()));
        row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_TERMINAL_TIME,
            Bytes.toBytes(gpsData.getTerminalTime().getTime()));
        if (Objects.nonNull(gpsData.getName())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_NAME,
                Bytes.toBytes(gpsData.getName()));
        }
        if (Objects.nonNull(gpsData.getReissue())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_REISSUE,
                Bytes.toBytes(gpsData.getReissue()));
        }
        if (Objects.nonNull(gpsData.getPlatformCode())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_PLATFORM_CODE,
                Bytes.toBytes(gpsData.getPlatformCode()));
        }
        if (Objects.nonNull(gpsData.getGpsSpeed())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_GPS_SPEED,
                Bytes.toBytes(gpsData.getGpsSpeed()));
        }
        if (Objects.nonNull(gpsData.getLongitude())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_LONGITUDE,
                Bytes.toBytes(gpsData.getLongitude()));
        }
        if (Objects.nonNull(gpsData.getLatitude())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_LATITUDE,
                Bytes.toBytes(gpsData.getLatitude()));
        }
        if (Objects.nonNull(gpsData.getOriginalLongitude())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_ORIGINAL_LONGITUDE,
                Bytes.toBytes(gpsData.getOriginalLongitude()));
        }
        if (Objects.nonNull(gpsData.getOriginalLatitude())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_ORIGINAL_LATITUDE,
                Bytes.toBytes(gpsData.getOriginalLatitude()));
        }
        if (Objects.nonNull(gpsData.getAltitude())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_ALTITUDE,
                Bytes.toBytes(gpsData.getAltitude()));
        }
        if (Objects.nonNull(gpsData.getDirection())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_DIRECTION,
                Bytes.toBytes(gpsData.getDirection()));
        }
        if (Objects.nonNull(gpsData.getSpeed())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_SPEED,
                Bytes.toBytes(gpsData.getSpeed()));
        }
        if (Objects.nonNull(gpsData.getMileage())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_MILEAGE,
                Bytes.toBytes(gpsData.getMileage()));
        }
        if (Objects.nonNull(gpsData.getStatus())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_STATUS,
                Bytes.toBytes(gpsData.getStatus()));
        }
        if (Objects.nonNull(gpsData.getAlarm())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_ALARM,
                Bytes.toBytes(gpsData.getAlarm()));
        }
        if (Objects.nonNull(gpsData.getReceiveTime())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_RECEIVE_TIME,
                dateToBytes(gpsData.getReceiveTime()));
        }
        if (Objects.nonNull(gpsData.getOverSpeed())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_OVER_SPEED,
                Bytes.toBytes(gpsData.getOverSpeed()));
        }
        if (Objects.nonNull(gpsData.getFatigueDriving())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_FATIGUE_DRIVING,
                Bytes.toBytes(gpsData.getFatigueDriving()));
        }
        if (Objects.nonNull(gpsData.getAcc())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_ACC_STATE,
                Bytes.toBytes(gpsData.getAcc()));
        }
        if (Objects.nonNull(gpsData.getIndustry())) {
            row.addColumn(GpsHBaseTableConst.COLUMN_FAMILY, GpsHBaseTableConst.COLUMN_INDUSTRY,
                Bytes.toBytes(gpsData.getIndustry()));
        }
        return row;
    }

    public static byte[] intToBytes(Integer value) {
        if (value != null) {
            return Bytes.toBytes(value);
        }
        return null;
    }

    public static byte[] doubleToBytes(Double value) {
        if (value != null) {
            return Bytes.toBytes(value);
        }
        return null;
    }

    public static byte[] longToBytes(Long value) {
        if (value != null) {
            return Bytes.toBytes(value);
        }
        return null;
    }

    public static byte[] strToBytes(String value) {
        if (value != null) {
            return Bytes.toBytes(value);
        }
        return null;
    }

    public static byte[] dateToBytes(Date value) {
        if (value != null) {
            return Bytes.toBytes(value.getTime());
        }
        return null;
    }

    private static Date bytesToDate(byte[] bytes) {
        if (ArrayUtil.isNotEmpty(bytes)) {
            return DateUtil.date(bytesToLong(bytes));
        }
        return null;
    }
}
