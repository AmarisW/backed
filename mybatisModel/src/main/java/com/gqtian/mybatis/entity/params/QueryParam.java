package com.gqtian.mybatis.entity.params;

import java.util.List;

import lombok.Data;

/**
 * 🤨 查询参数
 * @className BooksQueryParam
 * @author gqtian
 * @date 2025/12/29 17:39
 */
@Data
public class QueryParam {

    /**
     *  书名
     */
    private String title;

    /**
     * 书籍 Ids
     */
    private List<String> bookIds;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 借阅时间 - 开始
     */
    private String borrowTimeStart;

    /**
     * 借阅时间 - 结束
     */
    private String borrowTimeEnd;
}
