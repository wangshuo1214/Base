package com.ws.base.entity;

import lombok.Data;

@Data
public class LoginBody {


    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;
}
