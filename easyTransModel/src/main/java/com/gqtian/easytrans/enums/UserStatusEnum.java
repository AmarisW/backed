package com.gqtian.easytrans.enums;

import lombok.Getter;

/**
 * 用户状态枚举（ENUM 翻译示例）
 */
@Getter
public enum UserStatusEnum {

    NORMAL(1, "正常"),
    DISABLED(2, "禁用");

    private final int code;
    private final String label;

    UserStatusEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static UserStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
