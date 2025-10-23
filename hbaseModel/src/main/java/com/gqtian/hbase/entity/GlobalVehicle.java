/** Copyright (c) 2020 linkcld.com to Present. All rights reserved. */
package com.gqtian.hbase.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("GLOBAL_VEHICLE")
public class GlobalVehicle {

    @TableField("PLATE_NUMBER")
    private String plateNumber;

    @TableField("PLATE_COLOR")
    private Integer plateColor;

    @TableField("INDUSTRY")
    private String industry;
}
