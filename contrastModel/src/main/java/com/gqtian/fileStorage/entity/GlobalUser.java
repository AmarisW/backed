package com.gqtian.fileStorage.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 👻 用户
 * 
 * @className User
 * @author gqtian
 * @date 2025/8/14 23:53
 */
@Data
@TableName("global_user")
public class GlobalUser {
    
    @TableId
    @ExcelProperty("序号")
    private Long id;
    
    @ExcelProperty("用户名")
    private String username;
    
    @ExcelProperty("密码")
    private String password;
    
    @ExcelProperty("昵称")
    private String nickname;
}
