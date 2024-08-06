package com.ws.base.util;

import cn.hutool.setting.dialect.Props;

public class MessageUtil {

    private static Props props = new Props("message.properties","UTF-8");

    public static String getMessage(String key){
        return props.getStr(key);
    }
}
