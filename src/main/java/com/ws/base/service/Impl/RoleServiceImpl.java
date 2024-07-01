package com.ws.base.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ws.base.constant.BaseConstant;
import com.ws.base.constant.HttpStatus;
import com.ws.base.exception.BaseException;
import com.ws.base.mapper.RoleMapper;
import com.ws.base.mapper.RoleMenuMapper;
import com.ws.base.mapper.UserRoleMapper;
import com.ws.base.model.Role;
import com.ws.base.model.RoleMenu;
import com.ws.base.model.UserRole;
import com.ws.base.service.IRoleService;
import com.ws.base.util.InitFieldUtil;
import com.ws.base.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    RoleMenuMapper roleMenuMapper;

    @Autowired
    UserRoleMapper userRoleMapper;

    @Override
    @Transactional
    public boolean addRole(Role role) {
        if (checkField(role)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        //检查角色名称是否重复
        if (checkNameRepeat(role)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("role.nameRepeat"));
        }
        //初始化基本上属性
        if(!InitFieldUtil.initField(role)){
            throw new BaseException(HttpStatus.ERROR,MessageUtil.getMessage("initFieldError"));
        }
        role.setId(UUID.randomUUID().toString());

        //插入角色菜单表
        if(CollUtil.isNotEmpty(role.getMenuIds())){
            List<RoleMenu> roleMenus = new ArrayList<>();
            role.getMenuIds().forEach(menuId -> {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setId(UUID.randomUUID().toString());
                roleMenu.setRoleId(role.getId());
                roleMenu.setMenuId(menuId);
                roleMenus.add(roleMenu);
            });
            roleMenuMapper.batchAddRoleMenu(roleMenus);
        }
        return save(role);
    }

    @Override
    public List<Role> queryRole(Role role) {
        QueryWrapper<Role> wrapper = new QueryWrapper();
        wrapper.lambda().eq(Role::getDeleted, BaseConstant.FALSE);
        if(StrUtil.isNotEmpty(role.getRoleName())){
            wrapper.lambda().like(Role::getRoleName,role.getRoleName().trim());
        }
        if(StrUtil.isNotEmpty(role.getRoleKey())){
            wrapper.lambda().like(Role::getRoleKey,role.getRoleKey().trim());
        }
        return list(wrapper);
    }

    @Override
    public Role getRole(String id) {
        if (StrUtil.isEmpty(id)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        Role role = getById(id);

        if (ObjectUtil.isNull(role) || StrUtil.equals(role.getDeleted(), BaseConstant.TRUE)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("role.notexist"));
        }
        List<RoleMenu> roles = roleMenuMapper.selectRoleMenuByRoleId(id);
        role.setMenuIds(roles.stream().map(RoleMenu::getMenuId).collect(Collectors.toList()));
        return role;
    }

    @Override
    @Transactional
    public boolean updateRole(Role newRole) {
        if (checkField(newRole)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }
        if (checkNameRepeat(newRole)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("role.nameRepeat"));
        }
        //旧对象
        Role oldRole = getById(newRole.getId());
        //新旧对象不同，需要修改
        if (!updateFlag(newRole, oldRole)){
            oldRole.setRoleName(newRole.getRoleName());
            oldRole.setRoleKey(newRole.getRoleKey());
            oldRole.setOrderNum(newRole.getOrderNum());
            oldRole.setMenuCheckStrictly(newRole.getMenuCheckStrictly());
            oldRole.setRemark(newRole.getRemark());
            oldRole.setUpdateDate(new Date());
        }
        //单独处理角色关联的菜单
        if(!CollUtil.isEqualList(newRole.getMenuIds(), oldRole.getMenuIds())){
            //删除已存在的角色菜单关联关系
            roleMenuMapper.deleteRoleMenuByRoleId(newRole.getId());
            if (CollUtil.isNotEmpty(newRole.getMenuIds())){
                //新增角色菜单关联关系
                List<RoleMenu> roleMenus = new ArrayList<>();
                newRole.getMenuIds().forEach(menuId -> {
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setId(UUID.randomUUID().toString());
                    roleMenu.setRoleId(newRole.getId());
                    roleMenu.setMenuId(menuId);
                    roleMenus.add(roleMenu);
                });
                roleMenuMapper.batchAddRoleMenu(roleMenus);
            }
        }

        return updateById(oldRole);
    }

    @Override
    @Transactional
    public boolean deleteRole(List<String> ids) {
        if (CollUtil.isEmpty(ids)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }

        if (CollUtil.isNotEmpty(userRoleMapper.queryUserRolesByRoleIds(ids))){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("role.roleRelateUser"));
        }
        List<Role> roles = listByIds(ids);
        if (CollUtil.isEmpty(roles)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("role.notexist"));
        }
        roles.forEach(role -> {
            role.setDeleted(BaseConstant.TRUE);
            role.setUpdateDate(new Date());
        });

        //删除角色的同时，删除角色菜单的关联关系
        roleMenuMapper.batchDeleteRoleMenuByRoleIds(ids);

        return updateBatchById(roles);
    }

    @Override
    public int allocatedUsers(Role role) {
        if (ObjectUtil.isEmpty(role) || StrUtil.isEmpty(role.getId()) || CollUtil.isEmpty(role.getUserIds())){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }
        List<UserRole> urs = new ArrayList<>();
        role.getUserIds().forEach(item -> {
            if (StrUtil.isEmpty(item)){
                throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
            }
            UserRole ur = new UserRole();
            InitFieldUtil.initField(ur);
            ur.setRoleId(role.getId());
            ur.setUserId(item);
            urs.add(ur);
        });
        return userRoleMapper.allocateUsers(urs);
    }

    @Override
    public int unAllocatedUsers(Role role) {
        if (ObjectUtil.isEmpty(role) || StrUtil.isEmpty(role.getId()) || CollUtil.isEmpty(role.getUserIds())){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }
        return userRoleMapper.unAllocateUsers(role);
    }

    private boolean checkField(Role role){
        if (StrUtil.hasEmpty(role.getRoleName(),role.getRoleKey()) ||
                ObjectUtil.hasEmpty(role.getOrderNum(),role.getMenuCheckStrictly())
        ){
            return true;
        }
        return false;
    }

    private boolean checkNameRepeat(Role role){
        //检查角色名称是否重复
        QueryWrapper<Role> wrapper = new QueryWrapper();
        wrapper.lambda().eq(Role::getRoleName,role.getRoleName().trim());
        wrapper.lambda().eq(Role::getDeleted, BaseConstant.FALSE);
        if (StrUtil.isNotEmpty(role.getId())){
            wrapper.lambda().ne(Role::getId,role.getId());
        }
        if (CollUtil.isNotEmpty(list(wrapper))){
            return true;
        }
        return false;
    }

    private boolean updateFlag(Role newRole, Role oldRole){
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        sb1.append(newRole.getRoleName());
        sb2.append(oldRole.getRoleName());
        sb1.append(newRole.getRoleKey());
        sb2.append(oldRole.getRoleKey());
        sb1.append(newRole.getOrderNum());
        sb2.append(oldRole.getOrderNum());
        sb1.append(newRole.getMenuCheckStrictly());
        sb2.append(oldRole.getMenuCheckStrictly());
        sb1.append(newRole.getRemark());
        sb2.append(oldRole.getRemark());
        return sb1.toString().equals(sb2.toString());
    }
}
