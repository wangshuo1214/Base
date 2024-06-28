package com.ws.base.entity;

import lombok.Data;

@Data
public class Result {
    //返回code
    private Integer code;
    //返回描述
    private String message;
    //返回数据
    private Object data;

    /**
     * 返回消息代码 code 和 message
     * @param code
     * @param message
     * @return
     */
    public static Result returnCodeMessage(Integer code, String message){
        return returnCodeMessage(code,message,null);
    }

    /**
     * 返回消息 code message 和 data
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static Result returnCodeMessage(Integer code, String message, Object data){
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}
