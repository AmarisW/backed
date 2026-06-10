package com.gqtian.easytrans.utils;

import java.util.Collections;
import java.util.List;

import com.gqtian.common.globalProcess.GlobalException;
import lombok.Setter;
import org.dromara.core.trans.vo.VO;
import org.dromara.trans.service.impl.TransService;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.metadata.IPage;

import lombok.extern.slf4j.Slf4j;

/**
 * 翻译工具类
 * <p>
 * 手动翻译时统一入口，内部自动判断传入类型并调用对应翻译方法
 * </p>
 *
 * @author gqtian
 */
@Slf4j
public class TransUtil {

    /**
     * -- SETTER --
     * 设置 TransService（由 Spring 启动时注入）
     */
    @Setter
    private static TransService transService;

    /**
     * 获取 TransService 实例
     */
    private static TransService getTransService() {
        if (transService == null) {
            throw new GlobalException("TransService 未注入，请确保 TransUtilInitializer 已加载");
        }
        return transService;
    }

    /**
     * 统一翻译入口，自动判断类型
     *
     * @param target 待翻译对象（支持单个对象、List、IPage）
     * @param <T>    泛型
     * @return 翻译后的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T translate(T target) {
        if (target == null) {
            return null;
        }
        TransService ts = getTransService();

        if (target instanceof List<?> list) {
            return (T) translateList(list, ts);
        }

        if (target instanceof IPage<?> page) {
            return (T) translatePage(page, ts);
        }

        return translateObject(target, ts);
    }

    /**
     * 翻译单个对象
     */
    private static <T> T translateObject(T obj, TransService ts) {
        try {
            ts.transOne((VO) obj);
        } catch (Exception e) {
            log.error("翻译单个对象失败，对象类型：{}, 错误信息：{}", obj.getClass().getName(), e.getMessage(), e);
        }
        return obj;
    }

    /**
     * 翻译 List
     */
    @SuppressWarnings("unchecked")

    private static <T> List<T> translateList(List<T> list, TransService ts) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        try {
            ts.transBatch((List<? extends VO>) list);
        } catch (Exception e) {
            log.error("翻译列表失败，列表大小：{}, 错误信息：{}", list.size(), e.getMessage(), e);
        }
        return list;
    }

    /**
     * 翻译 IPage
     */
    @SuppressWarnings("unchecked")
    private static <T> IPage<T> translatePage(IPage<T> page, TransService ts) {
        if (page != null && !CollectionUtils.isEmpty(page.getRecords())) {
            try {
                ts.transBatch((List<? extends VO>) page.getRecords());
            } catch (Exception e) {
                log.error("翻译分页失败，页码：{}, 错误信息：{}", page.getCurrent(), e.getMessage(), e);
            }
        }
        return page;
    }
}