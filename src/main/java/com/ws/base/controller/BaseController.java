package com.ws.base.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.ws.base.constant.HttpStatus;
import com.ws.base.entity.PageDomain;
import com.ws.base.entity.PageQuery;
import com.ws.base.entity.Result;
import com.ws.base.entity.TableDataInfo;
import com.ws.base.exception.BaseException;
import com.ws.base.model.User;
import com.ws.base.service.IUserService;
import com.ws.base.util.GsonUtil;
import com.ws.base.util.JwtTokenUtil;
import com.ws.base.util.MessageUtil;
import com.ws.base.util.ServletUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

public class BaseController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private IUserService iUserService;

    public Result success(){
        return Result.returnCodeMessage(HttpStatus.SUCCESS, MessageUtil.getMessage("sucess"));
    }

    public Result success(Object data) {
        return Result.returnCodeMessage(HttpStatus.SUCCESS,MessageUtil.getMessage("sucess"),data);
    }

    public Result error() {
        return Result.returnCodeMessage(HttpStatus.ERROR,MessageUtil.getMessage("false"));
    }

    public Result error(String msg){
        return Result.returnCodeMessage(HttpStatus.ERROR,msg,null);
    }

    public Result computeResult(Object t){
        if ( t instanceof Boolean){
            return (boolean)t ? success() : error();
        }else if (t instanceof Integer){
            return (int) t > 0 ? success() : error();
        }
        return error(MessageUtil.getMessage("computedResultError"));
    }

    public Result diyResut(Integer code, String msg){
        return Result.returnCodeMessage(code,msg);
    }

    public Result diyResut(Integer code, String msg, Object data){
        return Result.returnCodeMessage(code,msg,data);
    }

    protected void startPage(PageQuery pageQuery){
        if (ObjectUtil.isEmpty(pageQuery)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        PageDomain page = pageQuery.getPage();
        if (ObjectUtil.hasEmpty(page.getPageNum(), page.getPageSize())){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("bparamsError"));
        }
        Integer pageNum = page.getPageNum();
        Integer pageSize = page.getPageSize();
        String orderBy = page.getOrderBy();
        PageHelper.startPage(pageNum, pageSize, orderBy);
    }

    protected TableDataInfo formatTableData(List<?> formatList){
        TableDataInfo rspData = new TableDataInfo();
        rspData.setRows(formatList);
        rspData.setTotal(new PageInfo(formatList).getTotal());
        return rspData;
    }

    protected <T> T getPageItem(PageQuery pageQuery,Class<T> clazz){
        Gson gson = GsonUtil.createGson();
        return gson.fromJson(gson.toJson(pageQuery.getItem()), clazz);
    }

    public User getUser(){
        String token = ServletUtils.getHeader("Authorization");
        //token解析结果
        String userId = "";
        try {
            Claims claims = jwtTokenUtil.getClaimsFromToken(token);
            userId = (String) claims.get("userId");
            //删除缓存的token
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        if (StrUtil.isNotEmpty(userId)){
            return iUserService.getById(userId);
        }
        return null;
    }

    public String getUserId(){
        User user = getUser();
        if (ObjectUtil.isNotEmpty(user)){
            return user.getId();
        }else {
            return null;
        }

    }



}
