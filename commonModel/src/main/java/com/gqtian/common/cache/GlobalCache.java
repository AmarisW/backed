package com.gqtian.common.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.gqtian.common.model.GlobalDict;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局缓存管理类
 * <p>
 * 提供全局字典数据的内存缓存，用于提高字典数据的访问性能
 * 在应用启动时从数据库加载字典数据到内存中，减少数据库查询次数
 * </p>
 * 
 * @author gqtian
 * @date 2025/10/31 17:06
 */
@Slf4j
public class GlobalCache {

    /**
     * 字典缓存容器（线程安全）
     * <p>
     * Key: 字典类型（type），例如："gender"、"status"
     * Value: 该类型下的所有字典项列表
     * </p>
     */
    public static final ConcurrentHashMap<String, List<GlobalDict>> GLOBAL_DICT = new ConcurrentHashMap<>();

    private GlobalCache() {
        // 私有构造函数，防止实例化
    }

    /**
     * 添加字典缓存
     * <p>
     * 如果指定类型的字典缓存已存在，则将新字典项追加到现有列表中；
     * 如果不存在，则创建新的字典类型并添加字典项列表
     * </p>
     *
     * @param type           字典类型
     * @param globalDictList 字典项列表
     */
    public static void addDicCache(String type, List<GlobalDict> globalDictList) {
        GLOBAL_DICT.computeIfAbsent(type, k -> new ArrayList<>()).addAll(globalDictList);
    }

    /**
     * 获取指定类型的字典列表
     *
     * @param type 字典类型
     * @return 字典列表，不存在则返回空列表
     */
    public static List<GlobalDict> getDictList(String type) {
        List<GlobalDict> list = GLOBAL_DICT.get(type);
        return list == null ? Collections.emptyList() : new ArrayList<>(list);
    }

    /**
     * 移除指定类型的字典缓存
     *
     * @param type 字典类型
     */
    public static void removeDicCache(String type) {
        GLOBAL_DICT.remove(type);
    }

    /**
     * 清空所有字典缓存
     */
    public static void clearAll() {
        GLOBAL_DICT.clear();
    }

    /**
     * 获取缓存的字典类型数量
     *
     * @return 字典类型数量
     */
    public static int size() {
        return GLOBAL_DICT.size();
    }
}
