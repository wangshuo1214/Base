package com.ws.base.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;

public class PasswordUtil {
    private static String passwordPrefix = "base_pwd:";

    public static String pwdEncrypt(String data){
        if (StrUtil.isEmpty(data)){
            return "";
        }
        return SmUtil.sm3(SecureUtil.md5(passwordPrefix+data));
    }
}
