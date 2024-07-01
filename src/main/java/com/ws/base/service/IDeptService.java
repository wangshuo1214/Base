package com.ws.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ws.base.entity.TreeSelect;
import com.ws.base.model.Dept;

import java.util.List;

public interface IDeptService extends IService<Dept> {

    boolean addDept(Dept bmDept);

    List<Dept> queryDept(Dept bmDept);

    boolean deleteDept(String id);

    boolean updateDept(Dept dept);

    Dept getDept(String Id);

    List<Dept> queryDeptExcludeChild(String id);

    TreeSelect getDeptTree();
}
