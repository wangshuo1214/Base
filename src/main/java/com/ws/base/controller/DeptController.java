package com.ws.base.controller;

import com.ws.base.entity.Result;
import com.ws.base.entity.TreeSelect;
import com.ws.base.model.Dept;
import com.ws.base.service.IDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dept")
public class DeptController extends BaseController{

    @Autowired
    private IDeptService iDeptService;

    @PostMapping("/add")
    public Result addDept(@RequestBody Dept dept){
        return computeResult(iDeptService.addDept(dept));
    }

    @PostMapping("/query")
    public Result queryDept(@RequestBody Dept dept){
        return success(iDeptService.queryDept(dept));
    }

    @GetMapping("/get")
    public Result getDept(String id){
        return success(iDeptService.getDept(id));
    }

    @PostMapping("/delete")
    public Result deleteDept(String id){
        return computeResult(iDeptService.deleteDept(id));
    }

    @PostMapping("/update")
    public Result updateDept(@RequestBody Dept dept){
        return computeResult(iDeptService.updateDept(dept));
    }

    @PostMapping("/exclude")
    public Result queryDeptExcludeChild(String id){
        return success(iDeptService.queryDeptExcludeChild(id));
    }

    @GetMapping("/deptTree")
    public Result getDeptTree(){
        List<TreeSelect> result = new ArrayList<>();
        result.add(iDeptService.getDeptTree());
        return success(result);
    }
}
