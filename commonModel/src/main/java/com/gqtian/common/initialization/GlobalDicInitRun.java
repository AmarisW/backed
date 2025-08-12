package com.gqtian.common.initialization;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gqtian.common.mapper.GlobalDicMapper;
import com.gqtian.common.model.GlobalDic;
import com.gqtian.common.utils.SpringApplicationUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 🔔 字典数据初始化到系统中
 *
 * @author GQTIAN
 * @date 2025/7/24 17:28
 **/
@Component
@Slf4j
@Order(2)
@ConditionalOnExpression("${spring.isEnable:true}")
// @ConditionalOnExpression("T(org.apache.commons.lang3.StringUtils).isNotEmpty('${spring
// .isEnable}')")
// @ConditionalOnExpression("'${spring.isEnable}'.isEmpty()")
// @ConditionalOnExpression("T(org.springframework.util.StringUtils).hasLength('${spring
// .isEnable}')")
// 判断注解上方是否有值
public class GlobalDicInitRun implements CommandLineRunner {
    
    // 字典缓存
    private static final Map<String, List<GlobalDic>> GLOBAL_DIC = new HashMap<>();
    
    /**
     * 初始化字典缓存
     */
    private static void initializeDictionary() {
        log.info("【字典缓存】字典缓存开始初始化！");
        LocalDateTime startTime = LocalDateTime.now();
        // 清空字典缓存
        GLOBAL_DIC.clear();
        // 获取字典SysDicDao实例
        GlobalDicMapper sysDicMapper = SpringApplicationUtil.getBean(GlobalDicMapper.class);
        LambdaQueryWrapper<GlobalDic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(GlobalDic::getCode, GlobalDic::getValue, GlobalDic::getType);
        
        List<GlobalDic> globalDicList = sysDicMapper.selectList(queryWrapper);
        
        for (GlobalDic globalDic : globalDicList) {
            // 如果当前Key存在,则直接将元素存储进去
            if (GLOBAL_DIC.containsKey(globalDic.getType())) {
                GLOBAL_DIC.get(globalDic.getType()).add(globalDic);
            }
            // 不存在,新建Key
            else {
                List<GlobalDic> globalDices = new ArrayList<>();
                globalDices.add(globalDic);
                GLOBAL_DIC.put(globalDic.getType(), globalDices);
            }
        }
        log.info("【字典缓存】字典缓存初始化完成！总数：{} 条，用时：{} ms",
            GLOBAL_DIC.size(),
            Duration.between(startTime, LocalDateTime.now()).toMillis());
    }
    
    /**
     * 获取指定字典
     *
     * @param type 字典类型
     * @param code 编码
     * @return 字典对象
     */
    public static GlobalDic getSysDic(String type, String code) {
        List<GlobalDic> globalDicList = listDictionary(type);
        if (!CollectionUtils.isEmpty(globalDicList)) {
            for (GlobalDic globalDic : globalDicList) {
                if (globalDic.getCode().equals(code)) {
                    return globalDic;
                }
            }
        }
        return null;
    }
    
    /**
     * 获取指定字典
     *
     * @param type 字典类型
     * @param code 编码
     * @return 字典对象
     */
    public static String getDicString(String type, String code) {
        List<GlobalDic> globalDicList = listDictionary(type);
        // 字符串转List<String>
        List<String> split = Arrays.asList(code.split(","));
        // 结果集合
        List<String> valueName = new ArrayList<>();
        
        if (!CollectionUtils.isEmpty(globalDicList)) {
            split.forEach(a -> globalDicList.forEach(b -> {
                if (b.getCode().equals(a)) {
                    valueName.add(b.getValue());
                }
            }));
        }
        
        return String.join(",", valueName);
    }
    
    /**
     * listDictionary:根据类型获取所有字典
     *
     * @param type 字典类型
     * @return List<SysDic>
     */
    public static List<GlobalDic> listDictionary(String type) {
        return GLOBAL_DIC.get(type);
    }
    
    /**
     * 更新缓存
     */
    public static void updateSysDic() {
        initializeDictionary();
    }
    
    /**
     * 系统启动时执行
     *
     * @param args 参数
     */
    @Override
    public void run(String... args) {
        initializeDictionary();
    }
    
}
