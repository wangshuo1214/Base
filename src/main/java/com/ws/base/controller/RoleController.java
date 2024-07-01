package com.ws.base.controller;

import com.ws.base.entity.PageQuery;
import com.ws.base.entity.Result;
import com.ws.base.model.Role;
import com.ws.base.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/role")
public class RoleController extends BaseController{

    @Autowired
    private IRoleService iRoleService;

    @PostMapping("/add")
    public Result addBmRole(@RequestBody Role role){
        return computeResult(iRoleService.addRole(role));
    }

    @PostMapping("/query")
    public Result queryRole(@RequestBody PageQuery pageQuery){
        startPage(pageQuery);
        Role role = getPageItem(pageQuery, Role.class);
        return success(formatTableData(iRoleService.queryRole(role)));
    }

    @GetMapping("/get")
    public Result getRole(String id){
        return success(iRoleService.getRole(id));
    }

    @PostMapping("/update")
    public Result updateRole(@RequestBody Role role){
        return computeResult(iRoleService.updateRole(role));
    }

    @PostMapping("/delete")
    public Result deleteRole(@RequestBody List<String> ids){
        return computeResult(iRoleService.deleteRole(ids));
    }

    @PostMapping("/allocate")
    public Result allocatedUsers(@RequestBody Role role){
        return computeResult(iRoleService.allocatedUsers(role));
    }

    @PostMapping("/unAllocate")
    public Result unAllocatedUsers(@RequestBody Role role){
        return computeResult(iRoleService.unAllocatedUsers(role));
    }
}
