package com.gqtian.easytrans.example.auto;

import org.dromara.core.trans.vo.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 讲师（AUTO 翻译数据源对象）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher implements VO {

    private Long id;

    private String name;

    private String title;
}
