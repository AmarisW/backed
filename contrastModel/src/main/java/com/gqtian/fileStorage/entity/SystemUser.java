package com.gqtian.fileStorage.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
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
@TableName("system_user")
public class SystemUser {
    
    @TableId
    @ExcelIgnore
    private Long id;
    
    @ExcelProperty("用户名")
    private String username;
    
    @ExcelProperty("密码")
    private String password;
    
    @ExcelProperty("昵称")
    private String nickname;
}
