package com.example.zydemo.utils.SubTableUtil;

public enum ErrorCodeEnum {

    NO_ANNOTATION_ERROR("10001", "missing annotation!", "缺少注解!");

    private String code;

    private String msg;

    private String desc;

    ErrorCodeEnum(String code, String msg, String desc) {
        this.code = code;
        this.msg = msg;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getDesc() {
        return desc;
    }
}
