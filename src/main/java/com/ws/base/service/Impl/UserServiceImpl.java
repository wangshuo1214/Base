package com.ws.base.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ws.base.config.BaseConfig;
import com.ws.base.constant.BaseConstant;
import com.ws.base.constant.HttpStatus;
import com.ws.base.exception.BaseException;
import com.ws.base.mapper.DeptMapper;
import com.ws.base.mapper.RoleMapper;
import com.ws.base.mapper.UserMapper;
import com.ws.base.mapper.UserRoleMapper;
import com.ws.base.model.Dept;
import com.ws.base.model.Role;
import com.ws.base.model.User;
import com.ws.base.service.IUserService;
import com.ws.base.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean addUser(User user) {
        if (checkFiled(user)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getUserName,user.getUserName());
        wrapper.lambda().eq(User::getDeleted, BaseConstant.FALSE);
        if (CollUtil.isNotEmpty(list(wrapper))){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("user.nameRepeat"));
        }
        if (!InitFieldUtil.initField(user)){
            throw new BaseException(HttpStatus.ERROR,MessageUtil.getMessage("initFieldError"));
        }
        user.setId(UUID.randomUUID().toString());
        user.setPassword(PasswordUtil.pwdEncrypt(user.getPassword()));
        return save(user);
    }

    @Override
    public List<User> queryUser(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getDeleted,BaseConstant.FALSE);
        if (StrUtil.isNotEmpty(user.getDeptId())){
            //查找出该部门的所有子部门
            List<String> deptAllChildren = getDeptAllChildren(Arrays.asList(user.getDeptId()));
            deptAllChildren.add(user.getDeptId());
            wrapper.lambda().in(User::getDeptId,deptAllChildren);
        }
        if (StrUtil.isNotEmpty(user.getUserName())){
            wrapper.lambda().like(User::getUserName,user.getUserName());
        }
        if (StrUtil.isNotEmpty(user.getRealName())){
            wrapper.lambda().like(User::getRealName,user.getRealName());
        }
        if (StrUtil.isNotEmpty(user.getStatus())){
            wrapper.lambda().eq(User::getStatus,user.getStatus());
        }
        List<User> result = list(wrapper);
        if (CollUtil.isNotEmpty(result)){
            result.stream().forEach(u ->{
                Dept bmDept = deptMapper.selectById(u.getDeptId());
                u.setDeptName(bmDept.getDeptName());
            });
        }
        return result;
    }

    @Override
    public boolean deleteUser(List<String> ids) {
        if (CollUtil.isEmpty(ids)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        //用户关联角色，不可删除
        if (CollUtil.isNotEmpty(userRoleMapper.queryUserRolesByUserIds(ids))){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("user.userRelateRole"));
        }
        List<User> delList = listByIds(ids);
        if (CollUtil.isEmpty(delList)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        delList.forEach(bmUser -> {
            bmUser.setDeleted(BaseConstant.TRUE);
            bmUser.setUpdateDate(new Date());
        });
        return updateBatchById(delList);
    }

    @Override
    public boolean updateUser(User newUser) {
        if (checkFiled(newUser)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        User oldBmUser = getById(newUser.getId());
        if (!updateFlag(newUser,oldBmUser)){
            oldBmUser.setRealName(newUser.getRealName());
            oldBmUser.setDeptId(newUser.getDeptId());
            oldBmUser.setStatus(newUser.getStatus());
            oldBmUser.setRemark(newUser.getRemark());
            oldBmUser.setUpdateDate(new Date());
        }
        return updateById(oldBmUser);
    }

    @Override
    public User getUser(String id) {
        if (StrUtil.isEmpty(id)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        User user = getById(id);
        if (ObjectUtil.isEmpty(user) || StrUtil.equals(user.getDeleted(),BaseConstant.TRUE)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        return user;
    }

    @Override
    public boolean resetUserPassword(String id) {
        if (StrUtil.isEmpty(id)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        User user = getById(id);
        if(ObjectUtil.isEmpty(user)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }

        user.setPassword(PasswordUtil.pwdEncrypt(BaseConstant.BASEPASSWORD));
        user.setUpdateDate(new Date());

        return updateById(user);
    }

    @Override
    public boolean changeUserStatus(String id, String status) {
        if (StrUtil.hasEmpty(id,status)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        if (!StrUtil.equals(status,BaseConstant.TRUE) && !StrUtil.equals(status,BaseConstant.FALSE)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("user.statusException"));
        }
        User user = getById(id);
        if (ObjectUtil.isEmpty(user) || StrUtil.equals(user.getDeleted(),BaseConstant.TRUE)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        user.setStatus(status);
        return updateById(user);
    }

    @Override
    public List<User> queryAllocatedUserList(User user) {
        if(ObjectUtil.isEmpty(user) || StrUtil.isEmpty(user.getRoleId())){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        List<String> userIds = userRoleMapper.queryUserIdsByRoleId(user.getRoleId());
        if (CollUtil.isEmpty(userIds)){
            return new ArrayList<>();
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(User::getId,userIds);
        if (StrUtil.isNotEmpty(user.getUserName())){
            wrapper.lambda().like(User::getUserName,user.getUserName());
        }
        if (StrUtil.isNotEmpty(user.getRealName())){
            wrapper.lambda().like(User::getRealName,user.getRealName());
        }
        wrapper.lambda().eq(User::getDeleted,BaseConstant.FALSE);
        List<User> results = list(wrapper);
        if(CollUtil.isNotEmpty(results)){
            results.stream().forEach(u ->{
                Dept dept = deptMapper.selectById(u.getDeptId());
                u.setDeptName(dept.getDeptName());
            });
        }
        return results;
    }

    @Override
    public List<User> queryUnAllocatedUserList(User user) {
        if(ObjectUtil.isEmpty(user) || StrUtil.isEmpty(user.getRoleId())){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        List<String> userIds = userRoleMapper.queryUserIdsByRoleId(user.getRoleId());
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (CollUtil.isNotEmpty(userIds)){
            wrapper.lambda().notIn(User::getId,userIds);
        }
        if (StrUtil.isNotEmpty(user.getUserName())){
            wrapper.lambda().like(User::getUserName,user.getUserName());
        }
        if (StrUtil.isNotEmpty(user.getRealName())){
            wrapper.lambda().like(User::getRealName,user.getRealName());
        }
        wrapper.lambda().eq(User::getDeleted,BaseConstant.FALSE);
        List<User> results = list(wrapper);
        if(CollUtil.isNotEmpty(results)){
            results.stream().forEach(u ->{
                Dept dept = deptMapper.selectById(u.getDeptId());
                u.setDeptName(dept.getDeptName());
            });
        }
        return results;
    }

    @Override
    public User getUserProfile(User user) {
        Dept dept = deptMapper.selectById(user.getDeptId());
        if (ObjectUtil.isNotEmpty(dept)){
            user.setDeptName(dept.getDeptName());
        }
        List<String> roleIds = userRoleMapper.queryRoleIdsByUserId(user.getId());
        if (CollUtil.isNotEmpty(roleIds)){
            StringBuffer sb = new StringBuffer("");
            for (int i =0; i < roleIds.size(); i++){
                Role bmRole = roleMapper.selectById(roleIds.get(i));
                sb.append(bmRole.getRoleName());
                if ((i + 1) != roleIds.size()){
                    sb.append("、");
                }
            }
            user.setRoleName(sb.toString());
        }
        return user;
    }

    @Override
    public boolean updateUserProfile(User user) {
        if(ObjectUtil.isEmpty(user) || StrUtil.isEmpty(user.getRealName())){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        User old = getById(user.getId());
        old.setRealName(user.getRealName());
        old.setUpdateDate(new Date());
        return updateById(old);
    }

    @Override
    public boolean updateUserPwd(User user) {
        if (ObjectUtil.isEmpty(user)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        // 原密码
        String oldPwd = String.valueOf(user.getParams().get("oldPwd"));
        // 新密码
        String newPwd = String.valueOf(user.getParams().get("newPwd"));
        // 确认密码
        String confirmPwd = String.valueOf(user.getParams().get("confirmPwd"));
        if (StrUtil.hasEmpty(oldPwd,newPwd,confirmPwd)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        // 新密码 与 确认密码 比较
        if (!StrUtil.equals(newPwd,confirmPwd)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("pwdNotEqualsError"));
        }
        User oldUser = getById(user.getId());
        if (!StrUtil.equals(PasswordUtil.pwdEncrypt(oldPwd),oldUser.getPassword())){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("pwdOldNewNotEqualsError"));
        }
        oldUser.setPassword(PasswordUtil.pwdEncrypt(newPwd));
        oldUser.setUpdateDate(new Date());

        redisUtil.deleteObject("userToken:"+oldUser.getId());

        return updateById(oldUser);
    }

    @Override
    public String uploadAvatar(MultipartFile file, User user) {
        if (file.isEmpty() || ObjectUtil.isEmpty(user)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        String avatarPath = FileUploadUtil.upload(BaseConfig.getAvatarPath(),file, Arrays.asList(BaseConstant.IMAGE_EXTENSION));
        if (StrUtil.isNotEmpty(avatarPath)){
            user.setAvatar(avatarPath);
            updateById(user);
            return avatarPath;
        }else {
            throw new BaseException(HttpStatus.ERROR, MessageUtil.getMessage("false"));
        }
    }

    //递归查询出该部门的所有子节点
    public List<String> getDeptAllChildren(List<String> parentIds){
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.lambda().select(Dept::getId);
        wrapper.lambda().eq(Dept::getDeleted,BaseConstant.FALSE);
        wrapper.lambda().in(Dept::getParentId,parentIds);
        List<String> ids = deptMapper.selectList(wrapper).stream().map(Dept::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(ids)){
            ids.addAll(getDeptAllChildren(ids));
            return ids;
        }else {
            return new ArrayList<>();
        }
    }

    private boolean updateFlag(User newObj, User oldObj){
        StringBuffer sb1 = new StringBuffer("");
        StringBuffer sb2 = new StringBuffer("");
        sb1.append(newObj.getRealName());
        sb2.append(oldObj.getRealName());
        sb1.append(newObj.getDeptId());
        sb2.append(oldObj.getDeptId());
        sb1.append(newObj.getStatus());
        sb2.append(oldObj.getStatus());
        sb1.append(newObj.getRemark());
        sb2.append(oldObj.getRemark());
        return sb1.toString().equals(sb2.toString());
    }

    private boolean checkFiled(User user){
        if (StrUtil.hasEmpty(user.getDeptId(),user.getUserName(),
                user.getPassword(), user.getRealName(),user.getStatus()
        )){
            return true;
        }
        return false;
    }
}
