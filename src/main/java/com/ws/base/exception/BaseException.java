package com.ws.base.exception;

public class BaseException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    /**
     * 服务器状态码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BaseException(Integer code) {
        this.code = code;
    }

    public BaseException(String message) {
        this.message = message;
    }

    public BaseException(Integer code, String message) {
        this.message = message;
        this.code = code;
    }
}

