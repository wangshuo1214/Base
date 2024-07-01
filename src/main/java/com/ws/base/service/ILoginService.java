package com.ws.base.service;

import com.ws.base.entity.LoginBody;
import com.ws.base.model.User;

import javax.servlet.http.HttpServletRequest;

public interface ILoginService {

    String login(LoginBody loginBody);

    User getUserInfoByToken(String token);

    Boolean logout(HttpServletRequest httpServletRequest);
}
