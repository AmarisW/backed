package com.gqtian.easytrans.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dromara.trans.service.impl.DictionaryTransService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.gqtian.easytrans.constant.EasyTransConstant;
import com.gqtian.easytrans.entity.SysDict;
import com.gqtian.easytrans.mapper.SysDictMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🤨 启动时将字典表数据刷新到 Easy-Trans Redis 字典翻译缓存
 * <p>
 * 配置 is-enable-redis=true 后，Easy-Trans 会自动使用 Redis 存储字典数据
 * </p>
 *
 * @className EasyTransDictInitRunner
 * @author gqtian
 * @date 2026/6/23
 */
@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class EasyTransDictInitRunner implements CommandLineRunner {

    private final SysDictMapper sysDictMapper;
    private final DictionaryTransService dictionaryTransService;

    @Override
    public void run(String... args) {
        int dbDictCount = 0;

        // 从数据库查询字典数据
        try {
            List<SysDict> dictList = sysDictMapper.selectList(null);
            Map<String, Map<String, String>> grouped = dictList.stream()
                    .collect(Collectors.groupingBy(SysDict::getDictType,
                            Collectors.toMap(SysDict::getDictCode, SysDict::getDictLabel, (left, right) -> left)));

            // 刷新到 Redis（当 is-enable-redis=true 时自动存储到 Redis）
            grouped.forEach(dictionaryTransService::refreshCache);
            dbDictCount = dictList.size();

            log.info("【Easy-Trans】字典数据已刷新到 Redis，共 {} 条", dbDictCount);
        } catch (Exception ex) {
            log.warn("【Easy-Trans】字典表加载失败，使用内存备用数据", ex);
            // 备用：使用内存数据
            dictionaryTransService.refreshCache(EasyTransConstant.DICT_GENDER, Map.of("0", "男", "1", "女"));
        }

        // 自定义字典（订单状态）
        dictionaryTransService.refreshCache(EasyTransConstant.DICT_ORDER_STATUS,
                Map.of("1", "待支付", "2", "已完成", "3", "已取消"));

        log.info("【Easy-Trans】字典缓存加载完成，数据库字典条数：{}，内存字典：order_status", dbDictCount);
    }
}
