package com.example.zydemo.utils.SubTableUtil;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2022/09/27 10:21
 * @Since jdk8+
 **/
public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private String code;
    private String msg;

    public BusinessException(String code, String message) {
        super("[errorCode=" + code + "] " + message);
        this.code = code;
        this.msg = message;
    }

    public BusinessException(String code, String message, Throwable cause) {
        super("[errorCode=" + code + "] " + message, cause);
        this.code = code;
        this.msg = message;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
