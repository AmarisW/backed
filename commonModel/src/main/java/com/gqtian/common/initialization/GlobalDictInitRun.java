package com.gqtian.common.initialization;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gqtian.common.cache.GlobalCache;
import com.gqtian.common.mapper.GlobalDicMapper;
import com.gqtian.common.model.GlobalDict;
import com.gqtian.common.utils.SpringApplicationUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 字典数据初始化到系统中
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
@ConditionalOnExpression("${spring.isEnable:false}") // 启用字典初始化 默认为不启用
// @ConditionalOnProperty(name = "spring.isEnable", havingValue = "true") // 其实推荐使用这个
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
            GlobalCache.clearAll();

            // 获取字典 Mapper 实例
            GlobalDicMapper sysDicMapper = SpringApplicationUtil.getBean(GlobalDicMapper.class);
            LambdaQueryWrapper<GlobalDict> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(GlobalDict::getCode, GlobalDict::getValue, GlobalDict::getType);

            List<GlobalDict> globalDictList = sysDicMapper.selectList(queryWrapper);

            // 按类型分组并添加到缓存
            Map<String, List<GlobalDict>> groupedDict =
                globalDictList.stream().collect(Collectors.groupingBy(GlobalDict::getType));

            groupedDict
                .forEach((type, dictList) -> GlobalCache.GLOBAL_DICT.put(type, new ArrayList<>(dictList)));

            log.info("【字典缓存】字典缓存初始化完成！类型数：{}，总条数：{}，用时：{} ms", GlobalCache.size(), globalDictList.size(),
                Duration.between(startTime, LocalDateTime.now()).toMillis());
        } catch (Exception e) {
            log.error("【字典缓存】字典缓存初始化异常！请检查字典表是否存在，或者修改配置文件启用字典配置", e);
        }
    }

}
