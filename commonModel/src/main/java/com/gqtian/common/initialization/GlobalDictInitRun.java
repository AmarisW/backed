package com.gqtian.common.initialization;

import static com.gqtian.common.cache.GlobalCache.GLOBAL_DICT;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gqtian.common.mapper.GlobalDicMapper;
import com.gqtian.common.model.GlobalDict;
import com.gqtian.common.utils.SpringApplicationUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 🔔 字典数据初始化到系统中
 * <p>
 * 该组件由 GlobalDatabaseAutoConfiguration 管理，会自动根据数据库配置启用
 * </p>
 * <p>
 * 可通过配置项 spring.isEnable=false 禁用字典初始化（默认启用）
 * </p>
 *
 * @author GQTIAN
 * @date 2025/7/24 17:28
 **/
@Slf4j
@Order(2)
@Component
@ConditionalOnExpression("${spring.isEnable:true}")
// @ConditionalOnExpression("T(org.apache.commons.lang3.StringUtils).isNotEmpty('${spring
// .isEnable}')")
// @ConditionalOnExpression("'${spring.isEnable}'.isEmpty()")
// @ConditionalOnExpression("T(org.springframework.util.StringUtils).hasLength('${spring
// .isEnable}')")
// 判断注解上方是否有值
public class GlobalDictInitRun implements CommandLineRunner {

    /**
     * 系统启动时执行
     *
     * @param args 参数
     */
    @Override
    public void run(String... args) {
        initializeDictionary();
    }

    /**
     * 初始化字典缓存
     */
    private static void initializeDictionary() {
        try {
            log.info("【字典缓存】字典缓存开始初始化！");
            LocalDateTime startTime = LocalDateTime.now();
            // 清空字典缓存
            GLOBAL_DICT.clear();
            // 获取字典 SysDicDao 实例
            GlobalDicMapper sysDicMapper = SpringApplicationUtil.getBean(GlobalDicMapper.class);
            LambdaQueryWrapper<GlobalDict> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(GlobalDict::getCode, GlobalDict::getValue, GlobalDict::getType);

            List<GlobalDict> globalDictList = sysDicMapper.selectList(queryWrapper);

            for (GlobalDict globalDict : globalDictList) {
                // 如果当前Key存在,则直接将元素存储进去
                if (GLOBAL_DICT.containsKey(globalDict.getType())) {
                    GLOBAL_DICT.get(globalDict.getType()).add(globalDict);
                }
                // 不存在,新建Key
                else {
                    List<GlobalDict> globalDices = new ArrayList<>();
                    globalDices.add(globalDict);
                    GLOBAL_DICT.put(globalDict.getType(), globalDices);
                }
            }
            log.info("【字典缓存】字典缓存初始化完成！总数：{} 条，用时：{} ms", GLOBAL_DICT.size(),
                Duration.between(startTime, LocalDateTime.now()).toMillis());
        } catch (Exception e) {
            log.error("【字典缓存】字典缓存初始化异常！请检查字典表是否存在，或者修改配置文件启用字典配置", e);
        }

    }

    /**
     * 获取指定字典
     *
     * @param type 字典类型
     * @param code 编码
     * @return 字典对象
     */
    public static GlobalDict getSysDic(String type, String code) {
        List<GlobalDict> globalDictList = listDictionary(type);
        if (!CollectionUtils.isEmpty(globalDictList)) {
            for (GlobalDict globalDict : globalDictList) {
                if (globalDict.getCode().equals(code)) {
                    return globalDict;
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
        List<GlobalDict> globalDictList = listDictionary(type);
        // 字符串转List<String>
        List<String> split = Arrays.asList(code.split(","));
        // 结果集合
        List<String> valueName = new ArrayList<>();

        if (!CollectionUtils.isEmpty(globalDictList)) {
            split.forEach(a -> globalDictList.forEach(b -> {
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
    public static List<GlobalDict> listDictionary(String type) {
        return GLOBAL_DICT.get(type);
    }

    /**
     * 更新缓存
     */
    public static void updateSysDic() {
        initializeDictionary();
    }

}
