package com.gqtian.common.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gqtian.common.model.GlobalDict;

import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 全局通用缓存
 * @className GlobalCache
 * @author gqtian
 * @date: 2025/10/31 17:06
 */
@Slf4j
public class GlobalCache {

    // 字典缓存
    public static final Map<String, List<GlobalDict>> GLOBAL_DICT = new HashMap<>();

    /**
     * 添加字段缓存（如果类型存在，则添加、如果不存再则新建）
     */
    public static void addDicCache(String type, List<GlobalDict> globalDictList) {
        if (GLOBAL_DICT.containsKey(type)) {
            GLOBAL_DICT.get(type).addAll(globalDictList);
        } else {
            GLOBAL_DICT.put(type, globalDictList);
        }
    }

}
