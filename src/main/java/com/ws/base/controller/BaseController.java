package com.ws.base.controller;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.ws.base.constant.HttpStatus;
import com.ws.base.entity.PageDomain;
import com.ws.base.entity.PageQuery;
import com.ws.base.entity.Result;
import com.ws.base.entity.TableDataInfo;
import com.ws.base.exception.BaseException;
import com.ws.base.util.GsonUtil;
import com.ws.base.util.JwtTokenUtil;
import com.ws.base.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

public class BaseController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public Result success(){
        return Result.returnCodeMessage(HttpStatus.SUCCESS, MessageUtil.getMessage("bm.sucess"));
    }

    public Result success(Object data) {
        return Result.returnCodeMessage(HttpStatus.SUCCESS,MessageUtil.getMessage("bm.sucess"),data);
    }

    public Result error() {
        return Result.returnCodeMessage(HttpStatus.ERROR,MessageUtil.getMessage("bm.false"));
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
        return error(MessageUtil.getMessage("bm.computedResultError"));
    }

    public Result diyResut(Integer code, String msg){
        return Result.returnCodeMessage(code,msg);
    }

    public Result diyResut(Integer code, String msg, Object data){
        return Result.returnCodeMessage(code,msg,data);
    }

    protected void startPage(PageQuery pageQuery){
        if (ObjectUtil.isEmpty(pageQuery)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("bm.paramsError"));
        }
        PageDomain page = pageQuery.getPage();
        if (ObjectUtil.hasEmpty(page.getPageNum(), page.getPageSize())){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("bm.paramsError"));
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




}
