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
 * 启动时将字典表数据刷新到 Easy-Trans 字典翻译缓存
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
        try {
            List<SysDict> dictList = sysDictMapper.selectList(null);
            Map<String, Map<String, String>> grouped = dictList.stream()
                .collect(Collectors.groupingBy(SysDict::getDictType,
                    Collectors.toMap(SysDict::getDictCode, SysDict::getDictLabel, (left, right) -> left)));
            grouped.forEach(dictionaryTransService::refreshCache);
            dbDictCount = dictList.size();
        } catch (Exception ex) {
            log.warn("【Easy-Trans】字典表加载失败，gender 字典改用内存数据", ex);
            dictionaryTransService.refreshCache(EasyTransConstant.DICT_GENDER, Map.of("0", "男", "1", "女"));
        }

        dictionaryTransService.refreshCache(EasyTransConstant.DICT_ORDER_STATUS,
            Map.of("1", "待支付", "2", "已完成", "3", "已取消"));

        log.info("【Easy-Trans】字典缓存加载完成，数据库字典条数：{}，内存字典：order_status", dbDictCount);
    }
}
