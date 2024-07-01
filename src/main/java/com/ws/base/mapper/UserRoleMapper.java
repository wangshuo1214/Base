package com.ws.base.mapper;

import com.ws.base.model.Role;
import com.ws.base.model.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper {

    /**
     * 根据角色id查询用户角色关联关系
     * @param roleIds
     * @return
     */
    List<UserRole> queryUserRolesByRoleIds(List<String> roleIds);

    /**
     * 根据用户id查询用户角色关联关系
     * @param userIds
     * @return
     */
    List<UserRole> queryUserRolesByUserIds(List<String> userIds);

    /**
     * 根据角色id查询关联用户id集合
     * @param roleId
     * @return
     */
    List<String> queryUserIdsByRoleId(String roleId);

    /**
     * 根据用户id获取角色id集合
     * @param userId
     * @return
     */
    List<String> queryRoleIdsByUserId(String userId);

    /**
     * 角色授权用户
     * @param userRoles
     * @return
     */
    int allocateUsers(List<UserRole> userRoles);

    /**
     * 取消角色授权
     * @param role
     * @return
     */
    int unAllocateUsers(Role role);
}
