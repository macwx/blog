package com.minzheng.blog.enums;

/**
 * @author blue
 * @date 2021/12/15
 * @apiNote
 */
public enum YesOrNoEnum {
    NO (2, "否"),

    YES (1, "是");

    //创建构造函数
    YesOrNoEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    //定义私有方法，获取枚举值
    private final Integer code;
    private final String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
