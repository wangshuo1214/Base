package com.ws.base.mapper;

import com.ws.base.model.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMenuMapper {

    /**
     * 批量新增角色菜单
     * @param roleMenuList
     * @return
     */
    int batchAddRoleMenu(List<RoleMenu> roleMenuList);

    /**
     * 通过角色ID删除角色和菜单关联
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int deleteRoleMenuByRoleId(String roleId);

    /**
     * 通过菜单ID删除角色和菜单关联
     * @param menuId
     * @return
     */
    int deleteRoleMenuByMenuId(String menuId);

    /**
     * 根据角色集合删除角色菜单关联
     * @param roleIds
     * @return
     */
    int batchDeleteRoleMenuByRoleIds(List<String> roleIds);

    /**
     * 根据角色ID查询角色菜单关联
     * @param roleId
     * @return
     */
    List<RoleMenu> selectRoleMenuByRoleId(String roleId);

    /**
     * 根据角色ID查询关联的菜单id
     *
     * */
    List<String> selectMenuIdsByRoleId(String bmRoleId);

    /**
     * 根据角色id集合查询菜单集合
     * @param bmRoleIds
     * @return
     */
    List<String> selectMenuIdsByRoleIds(List<String> bmRoleIds);

    /**
     * 根据角色ids查询关联菜单集合
     * @param bmRoleIds
     * @return
     */
    List<RoleMenu> selectMenusByRoleIds(List<String> bmRoleIds);

    /**
     * 根据菜单id查询角色菜单关联关系
     * @param bmMenuId
     * @return
     */
    List<RoleMenu> selectRoleMenuByMenuId(String bmMenuId);
}
