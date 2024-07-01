package com.ws.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ws.base.model.Role;

import java.util.List;

public interface IRoleService extends IService<Role> {
    boolean addRole(Role role);

    List<Role> queryRole(Role role);

    Role getRole(String id);

    boolean updateRole(Role role);

    boolean deleteRole(List<String> ids);

    int allocatedUsers(Role bmRole);

    int unAllocatedUsers(Role bmRole);
}
