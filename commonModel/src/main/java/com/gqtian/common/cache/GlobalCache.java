package com.gqtian.common.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gqtian.common.model.GlobalDic;

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
    public static final Map<String, List<GlobalDic>> GLOBAL_DIC = new HashMap<>();

    /**
     * 添加字段缓存（如果类型存在，则添加、如果不存再则新建）
     */
    public static void addDicCache(String type, List<GlobalDic> globalDicList) {
        if (GLOBAL_DIC.containsKey(type)) {
            GLOBAL_DIC.get(type).addAll(globalDicList);
        } else {
            GLOBAL_DIC.put(type, globalDicList);
        }
    }

}
