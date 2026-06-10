package com.gqtian.easytrans.config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.dromara.trans.service.impl.DictionaryTransService;
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
        List<SysDict> dictList = sysDictMapper.selectList(null);
        Map<String, Map<String, String>> grouped = dictList.stream()
            .collect(Collectors.groupingBy(SysDict::getDictType,
                Collectors.toMap(SysDict::getDictCode, SysDict::getDictLabel, (left, right) -> left)));

        grouped.forEach(dictionaryTransService::refreshCache);
        log.info("【Easy-Trans】字典缓存加载完成，类型数：{}，总条数：{}", grouped.size(), dictList.size());
    }
}
