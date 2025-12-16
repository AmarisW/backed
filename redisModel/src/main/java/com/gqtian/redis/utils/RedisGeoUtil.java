package com.gqtian.redis.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis Geo工具类
 * <p> 
 * 提供基于Redis的地理位置相关操作，包括：
 * 1. 添加地理位置信息
 * 2. 计算两个位置之间的距离
 * 3. 查找指定范围内的位置信息
 * 
 * @author gqtian
 * @date 2025/12/10 13:30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisGeoUtil {

    /**
     * RedisTemplate 实例，用于执行Redis操作
     */
    private final RedisTemplate redisTemplate;

    /**
     * 作为存储经纬度列表的key值
     * 所有地理位置信息都将存储在这个key下
     */
    private static final String GEO_KEY = "DISTANCE";

    /**
     * 将地理位置信息（经纬度）添加到Redis中
     * 
     * @param certId 标识符，用于唯一标识一个地理位置点
     * @param longitude 经度
     * @param latitude 纬度
     */
    public void geoAdd(String certId, double longitude, double latitude) {
        try {
            GeoOperations<String, Object> geoOperations = redisTemplate.opsForGeo();
            Point point = new Point(longitude, latitude);
            RedisGeoCommands.GeoLocation<Object> geoLocation = new RedisGeoCommands.GeoLocation<>(certId, point);
            geoOperations.add(GEO_KEY, geoLocation);
        } catch (Exception e) {
            log.error("添加地理位置信息失败, certId: {}, longitude: {}, latitude: {}", certId, longitude, latitude, e);
        }
    }

    /**
     * 计算两个地理位置点之间的距离
     * 
     * @param certId1 第一个位置点的标识符
     * @param certId2 第二个位置点的标识符
     * @return 两点之间的距离（单位：米），如果计算失败则返回-1
     */
    public double distanceBetween(String certId1, String certId2) {
        try {
            GeoOperations<String, Object> geoOperations = redisTemplate.opsForGeo();
            Distance distance = geoOperations.distance(GEO_KEY, certId1, certId2);
            return distance != null ? distance.getValue() : -1;
        } catch (Exception e) {
            log.error("计算两点间距离失败, certId1: {}, certId2: {}", certId1, certId2, e);
            return -1;
        }
    }

    /**
     * 查询距离指定位置点指定范围内的所有位置点，包括它们与目标点的距离
     * 
     * @param certId 目标位置点的标识符
     * @param distance 搜索半径（单位：米）
     * @return 包含范围内位置点及其距离的映射表，key为位置点标识符，value为距离（米）
     */
    public Map<String, Double> distanceInclude(String certId, double distance) {
        try {
            Map<String, Double> map = new LinkedHashMap<>();

            GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();
            RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
            GeoResults<GeoLocation<String>> geoResults =
                geoOperations.radius(GEO_KEY, certId, new Distance(distance), geoRadiusCommandArgs.includeDistance());

            if (geoResults != null) {
                for (GeoResult<GeoLocation<String>> geoResult : geoResults) {
                    // 与目标点相距的距离信息
                    Distance geoResultDistance = geoResult.getDistance();
                    // 该点的信息
                    GeoLocation<String> geoResultContent = geoResult.getContent();
                    map.put(geoResultContent.getName(), geoResultDistance.getValue());
                }
            }
            return map;
        } catch (Exception e) {
            log.error("查询范围内位置点失败, certId: {}, distance: {}", certId, distance, e);
            return Collections.emptyMap();
        }
    }
}