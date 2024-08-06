package com.ws.base.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ws.base.entity.LoginBody;
import com.ws.base.entity.Result;
import com.ws.base.model.User;
import com.ws.base.service.ILoginService;
import com.ws.base.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping
public class LoginController extends BaseController{

    @Autowired
    ILoginService iLoginService;

    @Autowired
    IMenuService iMenuService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginBody loginBody){

        String token = iLoginService.login(loginBody);

        if(StrUtil.isNotEmpty(token)){
            return success(token);
        }
        return error();
    }

    @GetMapping("/userInfo")
    public Result getUserInfoByToken(String token){

        User bmUser = iLoginService.getUserInfoByToken(token);

        if (ObjectUtil.isNotEmpty(bmUser)){
            return success(bmUser);
        }
        return error();
    }

    @PostMapping("/logout")
    public Result logout(HttpServletRequest httpServletRequest){
        Boolean flag = iLoginService.logout(httpServletRequest);
        if (flag){
            return success();
        }
        return error();
    }

    @GetMapping("/getRouters")
    public Result GetRouters(){
        return success(iMenuService.queryMenuTreeByUserId(getUser()));
    }
}
