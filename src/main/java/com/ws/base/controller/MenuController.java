package com.ws.base.controller;

import com.ws.base.entity.Result;
import com.ws.base.model.Menu;
import com.ws.base.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController{

    @Autowired
    private IMenuService iMenuService;

    @PostMapping("/add")
    public Result addMenu(@RequestBody Menu menu){
        return computeResult(iMenuService.addMenu(menu));
    }

    @PostMapping("/query")
    public Result queryMenu(@RequestBody Menu menu){
        return success(iMenuService.queryMenu(menu));
    }

    @GetMapping("/get")
    public Result getMenu(String id){
        return success(iMenuService.getMenu(id));
    }

    @PostMapping("/update")
    public Result updMenu(@RequestBody Menu menu){
        return computeResult(iMenuService.updMenu(menu));
    }

    @PostMapping("/delete")
    public Result deleteMenu(String id){
        return success(iMenuService.deleteMenu(id));
    }

    @PostMapping("/exclude")
    public Result queryMenuExcludeChild(String id){
        return success(iMenuService.queryMenuExcludeChild(id));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public Result treeselect(Menu menu) {
        List<Menu> menus = iMenuService.queryMenu(menu);
        return success(iMenuService.buildMenuTreeSelect(menus));
    }

    /**
     * 获取菜单下拉树列表以及已经选中的菜单
     */
    @GetMapping("/roleMenuTreeselect")
    public Result roleMenuTreeselect(String roleId){
        List<Menu> menus = iMenuService.queryMenu(new Menu());
        Map<String, Object> result = new HashMap<>();
        result.put("checkedKeys",iMenuService.selectMenuListByRoleId(roleId));
        result.put("menus",iMenuService.buildMenuTreeSelect(menus));
        return success(result);
    }
}
