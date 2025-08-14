package com.gqtian.fileStorage.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.LongAdder;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 👻 缓存数据
 *
 * @author gqtian
 * @className CacheData
 * @date 2024/6/4 10:04
 */
@Data
@Slf4j
@Component
public class CacheData {
    
    public static LongAdder flowCountStatic = new LongAdder();
    
    // 注意事项 依赖注入的方式 公用一个对象属性，而new 出来的对象属性是独立的
    // region 测试 final static 常见依赖依赖之间的关系
    public final LongAdder flowCountFinal = new LongAdder();
    
    /**
     * 关于大数据量推送缓存的示例
     * <p>
     * 用于存储数据的队列，使用LinkedBlockingDeque实现，具有线程安全的特性。 <a href=
     * "https://redis.blog.csdn.net/article/details/136579541?spm=1001.2101.3001.6650.3&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-3-136579541-blog-11617529.235%5Ev43%5Epc_blog_bottom_relevance_base7&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-3-136579541-blog-11617529.235%5Ev43%5Epc_blog_bottom_relevance_base7&utm_relevant_index=4">...
     * </a>
     */
    private final LinkedBlockingDeque<Object> DATA_QUEUE = new LinkedBlockingDeque<>();
    
    // endregion
    public LongAdder flowCountLong = new LongAdder();
    
    /**
     * 将数据添加到队列中。
     *
     * @param data 要添加到队列的数据对象。
     */
    public void add(Object data) {
        DATA_QUEUE.add(data);
    }
    
    /**
     * 清空队列中的所有数据，并返回这些数据。
     *
     * @return 包含队列中所有数据的列表。
     */
    public List<Object> drainTo() {
        // 创建一个列表，用于存储将要从队列中移除的所有元素
        List<Object> getList = new ArrayList<>(DATA_QUEUE.size());
        // 将队列中的所有元素移除并添加到getList中
        DATA_QUEUE.drainTo(getList);
        // 记录日志，记录本次移除操作的数量和队列剩余的数量
        log.info("本次取出 => {}条, 剩余 => {}", getList.size(), DATA_QUEUE.size());
        return getList;
    }
    
}
