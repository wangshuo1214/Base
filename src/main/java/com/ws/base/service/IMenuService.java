package com.ws.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ws.base.entity.RouterVo;
import com.ws.base.entity.TreeSelect;
import com.ws.base.model.Menu;
import com.ws.base.model.User;

import java.util.List;

public interface IMenuService extends IService<Menu> {

    boolean addMenu(Menu menu);

    List<Menu> queryMenu(Menu menu);

    Menu getMenu(String id);

    boolean updMenu(Menu newMenu);

    boolean deleteMenu(String id);

    List<Menu> queryMenuExcludeChild(String id);

    /**构建前端所需要树结构*/
    List<Menu> buildMenuTree(List<Menu> menus);

    /**构建前端所需要下拉树结构*/
    List<TreeSelect> buildMenuTreeSelect(List<Menu> menus);

    /**根据角色ID查询菜单树信息*/
    List<String> selectMenuListByRoleId(String roleId);

    /**
     * 根据用户id查询菜单树
     * @param user
     * @return
     */
    List<RouterVo> queryMenuTreeByUserId(User user);
}
