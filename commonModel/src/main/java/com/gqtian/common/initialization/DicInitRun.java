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
import com.gqtian.common.mapper.SysDicMapper;
import com.gqtian.common.model.SysDic;
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
public class DicInitRun implements CommandLineRunner {
    
    // 字典缓存
    private static final Map<String, List<SysDic>> SYS_DIC = new HashMap<>();
    
    /**
     * 初始化字典缓存
     */
    private static void initializeDictionary() {
        log.info("【字典缓存】字典缓存开始初始化！");
        LocalDateTime startTime = LocalDateTime.now();
        // 清空字典缓存
        SYS_DIC.clear();
        // 获取字典SysDicDao实例
        SysDicMapper sysDicMapper = SpringApplicationUtil.getBean(SysDicMapper.class);
        LambdaQueryWrapper<SysDic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysDic::getCode, SysDic::getValue, SysDic::getType);
        
        List<SysDic> sysDicList = sysDicMapper.selectList(queryWrapper);
        
        for (SysDic sysDic : sysDicList) {
            // 如果当前Key存在,则直接将元素存储进去
            if (SYS_DIC.containsKey(sysDic.getType())) {
                SYS_DIC.get(sysDic.getType()).add(sysDic);
            }
            // 不存在,新建Key
            else {
                List<SysDic> sysDices = new ArrayList<>();
                sysDices.add(sysDic);
                SYS_DIC.put(sysDic.getType(), sysDices);
            }
        }
        log.info("【字典缓存】字典缓存初始化完成！总数：{} 条，用时：{} ms",
            SYS_DIC.size(),
            Duration.between(startTime, LocalDateTime.now()).toMillis());
    }
    
    /**
     * 获取指定字典
     *
     * @param type 字典类型
     * @param code 编码
     * @return 字典对象
     */
    public static SysDic getSysDic(String type, String code) {
        List<SysDic> sysDicList = listDictionary(type);
        if (!CollectionUtils.isEmpty(sysDicList)) {
            for (SysDic sysDic : sysDicList) {
                if (sysDic.getCode().equals(code)) {
                    return sysDic;
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
        List<SysDic> sysDicList = listDictionary(type);
        // 字符串转List<String>
        List<String> split = Arrays.asList(code.split(","));
        // 结果集合
        List<String> valueName = new ArrayList<>();
        
        if (!CollectionUtils.isEmpty(sysDicList)) {
            split.forEach(a -> sysDicList.forEach(b -> {
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
    public static List<SysDic> listDictionary(String type) {
        return SYS_DIC.get(type);
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
