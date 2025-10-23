/**
 * HBase查询服务类，提供对HBase数据库中GPS数据的查询和修正功能。
 */
package com.gqtian.hbase.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gqtian.common.globalProcess.GlobalException;
import com.gqtian.hbase.config.HbaseConfig;
import com.gqtian.hbase.config.HbaseProperty;
import com.gqtian.hbase.entity.GlobalVehicle;
import com.gqtian.hbase.entity.GpsData;
import com.gqtian.hbase.util.GpsHBaseTableConverter;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HBaseSearchService {

    // 使用ConcurrentHashSet确保线程安全
    private static final Set<String> SUCCESS_CAR = ConcurrentHashMap.newKeySet();

    private static final List<GlobalVehicle> CAR_INFO = new ArrayList<>();

    private final ProducerService producerService;

    /**
     * HBase配置属性。
     */
    private final HbaseProperty hbaseProperty;

    /**
     * 基础车辆服务，用于获取车辆信息。
     */
    private final BasicCarService basicCarService;

    @Value("${hbase.path}")
    private String path;

    @Value("${hbase.table-name}")
    private String hbaseTableName;

    // 自定线程池
    @Resource(name = "dealThread")
    private ThreadPoolExecutor threadPool;

    private static List<GpsData> correctionRules(List<GpsData> gapDateList) {
        return gapDateList.parallelStream().peek(gpsData -> {
            gpsData.setSpeed(gpsData.getSpeed() / 10);
            gpsData.setGpsSpeed(gpsData.getGpsSpeed() / 10);
        }).collect(Collectors.toList());
    }

    // @PostConstruct
    public void init() {
        CAR_INFO.addAll(getGlobalVehicles(null, null));
        log.info("初始化车辆信息完成，车辆数量：{}", CAR_INFO.size());
    }

    /**
     * 对指定日期范围内的车辆GPS数据进行修正。
     *
     */
    public void correction(String beginTime, String endTime) {
        long startTime = System.currentTimeMillis();
        String terminalTime = DateUtil.format(DateUtil.parse(beginTime), "yyyy-MM-dd");
        readSuccessCar(terminalTime);
        // 日志记录修正的日期范围和参数
        log.info("时间范围：{} -> {}】", beginTime, endTime);
        // 日志记录需修正的基础数据数量
        log.info("本次需执行基础数据数量：{}", CAR_INFO.size());
        for (GlobalVehicle globalVehicle : CAR_INFO) {
            threadPool.execute(() -> {
                Table table = getTable();
                // 组合车辆车牌和颜色作为唯一标识
                String key = globalVehicle.getPlateNumber() + globalVehicle.getPlateColor();
                try {
                    // 检查是否已成功修正过，避免重复操作
                    if (SUCCESS_CAR.contains(key)) {
                        log.info("{}日车辆{}已经成功修改,不在继续执行！", terminalTime, key);
                    } else {
                        // 查询指定日期范围内的GPS数据
                        List<GpsData> gapDateList = getGpsData(globalVehicle.getPlateNumber(),
                            globalVehicle.getPlateColor(), beginTime, endTime, table);
                        // 日志记录查询到的GPS数据数量
                        log.info("{}日车辆{}查询到Gps数量：【{}】", terminalTime, key, gapDateList.size());
                        producerService.sendAsyncMsg(gapDateList);
                        // 保存修正后的GPS数据
                        // this.save(terminalTime, correctionRules(gapDateList), table, key);
                    }
                } catch (Exception e) {
                    log.error("{}修正失败", key, e);
                } finally {
                    // 关闭表连接
                    if (table != null) {
                        try {
                            table.close();
                        } catch (IOException e) {
                            log.error("【Hbase】关闭表失败", e);
                        }
                    }
                }
            });
        }
        log.info("{}修正耗时：{}", terminalTime, System.currentTimeMillis() - startTime);

    }

    /**
     * 根据车牌号和车牌颜色获取全球车辆信息列表。
     *
     * @param pn 车牌号。
     * @param pc 车牌颜色。
     * @return 车辆信息列表。
     */

    private List<GlobalVehicle> getGlobalVehicles(String pn, String pc) {
        List<GlobalVehicle> vehicleList = new ArrayList<>();
        if (StringUtils.isEmpty(pn) && StringUtils.isEmpty(pc)) {
            // 如果车牌号和车牌颜色都为空，则获取所有车辆信息
            vehicleList = basicCarService.getVehicleList();
        } else {
            // 否则，根据提供的车牌号和车牌颜色获取特定车辆信息
            GlobalVehicle globalVehicle = new GlobalVehicle();
            globalVehicle.setPlateNumber(pn);
            globalVehicle.setPlateColor(Integer.valueOf(pc));
            vehicleList.add(globalVehicle);
        }
        return vehicleList;
    }

    /**
     * 获取表连接
     *
     * @return table
     */
    private Table getTable() {
        Table table;
        Connection connection = HbaseConfig.connection;
        if (connection == null || connection.isClosed()) {
            HbaseConfig.init(hbaseProperty);
        }
        try {
            String tableName = hbaseTableName;
            table = HbaseConfig.connection.getTable(TableName.valueOf(tableName));
        } catch (Exception e) {
            throw new GlobalException("【Hbase】获取表失败");
        }
        return table;
    }

    /**
     * 根据车牌号、车牌颜色和时间范围从HBase中查询GPS数据。
     *
     * @param plateNumber 车牌号。
     * @param plateColor 车牌颜色。
     * @param beginTerminalTime 查询开始时间。
     * @param endTerminalTime 查询结束时间。
     * @param table 表对象。
     * @return GPS数据列表。
     */
    private List<GpsData> getGpsData(String plateNumber, Integer plateColor, String beginTerminalTime,
        String endTerminalTime, Table table) {
        final DateTime startDate = DateUtil.parse(beginTerminalTime);
        final DateTime endDate = DateUtil.parse(endTerminalTime);
        try {
            return getUsbGpsData(plateNumber, plateColor, table, startDate, endDate);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 将GPS数据保存到HBase表中。
     *
     * @param terminalTime 修正日期。
     * @param gpsDataList GPS数据列表。
     * @param table 表对象。
     * @param key 车辆标识。
     */
    public void save(String terminalTime, List<GpsData> gpsDataList, Table table, String key) {
        try {
            table.put(GpsHBaseTableConverter.getRow(gpsDataList));
            log.info("{}日车辆{}回写成功：【{}】", terminalTime, key, gpsDataList.size());
            writeSuccessCar(terminalTime, key);
        } catch (Exception e) {
            log.error("hbase连接异常,errMsg:{}", e.getMessage(), e);
        }
    }

    /**
     * 从HBase中查询指定时间范围内的USB GPS数据。
     *
     * @param plateNumber 车牌号。
     * @param plateColor 车牌颜色。
     * @param table 表对象。
     * @param startDate 查询开始时间。
     * @param endDate 查询结束时间。
     * @return GPS数据列表。
     */
    private List<GpsData> getUsbGpsData(String plateNumber, Integer plateColor, Table table, DateTime startDate,
        DateTime endDate) {
        try {
            Scan scan = new Scan();
            scan.setStartRow(GpsHBaseTableConverter.getRowKey(plateNumber, plateColor, startDate));
            scan.setStopRow(GpsHBaseTableConverter.getRowKey(plateNumber, plateColor, endDate));
            ResultScanner scanner = table.getScanner(scan);
            return GpsHBaseTableConverter.getResultList(scanner);
        } catch (Exception e) {
            log.error("hbase 查询异常", e);
            throw new GlobalException("hbase 查询异常：", e);
        } finally {
            try {
                if (table != null) {
                    table.close();
                }
            } catch (IOException e) {
                log.error("hbase 关闭异常", e);
            }
        }
    }

    /**
     * 将成功修正的车辆标识记录到文件中。
     *
     * @param dateString 日期字符串。
     * @param content 车辆标识内容。
     */
    /** 写文件操作 */
    public void writeSuccessCar(String dateString, String content) {
        FileUtil.appendUtf8String(content + "\n", path + dateString + ".txt");
    }

    private void readSuccessCar(String terminalTime) {
        // 读取指定天数文件数据进入缓存
        SUCCESS_CAR.clear();
        // 判断文件是否存在
        if (!FileUtil.exist(path + terminalTime + ".txt")) {
            log.info("{}日车辆修正记录文件不存在", terminalTime);
            return;
        }
        List<String> readLines = FileUtil.readLines(path + terminalTime + ".txt", "UTF-8");
        SUCCESS_CAR.addAll(readLines);
        log.info("{}日车辆修正记录文件读取成功，本次读取数据：【{}】", terminalTime, readLines.size());
    }

    public void send() {
        GpsData gpsData = new GpsData();
        gpsData.setId("1");
        gpsData.setName("1");
        gpsData.setPlateNumber("1");

        GpsData gpsData2 = new GpsData();
        gpsData2.setId("2");
        gpsData2.setName("2");
        gpsData2.setPlateNumber("2");

        ArrayList<GpsData> list = new ArrayList<>();
        list.add(gpsData);
        list.add(gpsData2);
        producerService.sendAsyncMsg(list);
    }
}
