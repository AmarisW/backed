package com.gqtian.common.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 字典缓存容器
     * <p>
     * Key: 字典类型（type），例如："gender"、"status"
     * Value: 该类型下的所有字典项列表
     * </p>
     */
    public static final Map<String, List<GlobalDict>> GLOBAL_DICT = new HashMap<>();

    /**
     * 添加字典缓存
     * <p>
     * 如果指定类型的字典缓存已存在，则将新字典项追加到现有列表中；
     * 如果不存在，则创建新的字典类型并添加字典项列表
     * </p>
     *
     * @param type 字典类型
     * @param globalDictList 字典项列表
     */
    public static void addDicCache(String type, List<GlobalDict> globalDictList) {
        if (GLOBAL_DICT.containsKey(type)) {
            GLOBAL_DICT.get(type).addAll(globalDictList);
        } else {
            GLOBAL_DICT.put(type, globalDictList);
        }
    }

}
