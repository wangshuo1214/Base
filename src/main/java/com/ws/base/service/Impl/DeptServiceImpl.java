package com.ws.base.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ws.base.constant.BaseConstant;
import com.ws.base.constant.HttpStatus;
import com.ws.base.entity.TreeSelect;
import com.ws.base.exception.BaseException;
import com.ws.base.mapper.DeptMapper;
import com.ws.base.mapper.UserMapper;
import com.ws.base.model.Dept;
import com.ws.base.model.User;
import com.ws.base.service.IDeptService;
import com.ws.base.util.InitFieldUtil;
import com.ws.base.util.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements IDeptService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean addDept(Dept dept) {
        if (checkFiled(dept)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        //没有父部门不能创建部门
        Dept parenDept = getById(dept.getParentId());
        if (!StrUtil.equals(dept.getParentId(), BaseConstant.FALSE) && (ObjectUtil.isEmpty(parenDept) || StrUtil.equals(parenDept.getDeleted(),BaseConstant.TRUE))){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("parentStatusError"));
        }
        //判断创建部门名称是否重复
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Dept::getDeptName,dept.getDeptName().trim());
        wrapper.lambda().eq(Dept::getDeleted,BaseConstant.FALSE);
        if (CollUtil.isNotEmpty(list(wrapper))){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("dept.nameRepeat"));
        }
        //初始化基本属性
        if (!InitFieldUtil.initField(dept)){
            throw new BaseException(HttpStatus.ERROR,MessageUtil.getMessage("initFieldError"));
        }
        dept.setId(UUID.randomUUID().toString());
        return save(dept);
    }

    @Override
    public List<Dept> queryDept(Dept dept) {

        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Dept::getDeleted,BaseConstant.FALSE);//删除标志
        //查询条件-部门名称
        if (StrUtil.isNotEmpty(dept.getDeptName())){
            wrapper.lambda().like(Dept::getDeptName,dept.getDeptName().trim());
        }
        //排序
        wrapper.lambda().orderByAsc(Dept::getOrderNum).orderByDesc(Dept::getUpdateTime);

        return list(wrapper);
    }

    @Override
    public boolean deleteDept(String id) {

        if (StrUtil.isEmpty(id)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }

        //如果有子部门，那么不能删除
        QueryWrapper<Dept> deptWrapper = new QueryWrapper<>();
        deptWrapper.lambda().eq(Dept::getParentId,id);
        deptWrapper.lambda().eq(Dept::getDeleted,BaseConstant.FALSE);
        List<Dept> childList = list(deptWrapper);
        if (CollUtil.isNotEmpty(childList)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("dept.hasChildren"));
        }

        //如果部门下面有用户，那么不能删除
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.lambda().eq(User::getDeptId,id);
        userWrapper.lambda().eq(User::getDeleted,BaseConstant.FALSE);
        List<User> userList = userMapper.selectList(userWrapper);
        if(CollUtil.isNotEmpty(userList)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("dept.hasUser"));
        }

        //对该部门进行删除
        Dept dept = getById(id);
        if (ObjectUtil.isEmpty(dept)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("dept.notexist"));
        }
        dept.setDeleted(BaseConstant.TRUE);
        dept.setUpdateTime(new Date());
        return updateById(dept);
    }

    @Override
    public boolean updateDept(Dept newDept) {
        if (checkFiled(newDept)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }

        //没有父部门不能修改部门
        Dept parenDept = getById(newDept.getParentId());
        if (ObjectUtil.isEmpty(parenDept)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("parentStatusError"));
        }
        //判断修改部门名称是否重复
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Dept::getDeptName,newDept.getDeptName().trim());
        wrapper.lambda().eq(Dept::getDeleted,BaseConstant.FALSE);
        wrapper.lambda().ne(Dept::getId,newDept.getId());
        List<Dept> repeatDepts = list(wrapper);
        if (CollUtil.isNotEmpty(repeatDepts)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("dept.nameRepeat"));
        }

        //旧对象
        Dept oldDept = getById(newDept.getId());
        if (!updateFlag(newDept,oldDept)){
            oldDept.setDeptName(newDept.getDeptName());
            oldDept.setParentId(newDept.getParentId());
            oldDept.setOrderNum(newDept.getOrderNum());
        }

        return updateById(oldDept);
    }

    @Override
    public Dept getDept(String id) {
        if (StrUtil.isEmpty(id)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }
        Dept dept = getById(id);

        if (ObjectUtil.isEmpty(dept) || StrUtil.equals(dept.getDeleted(),BaseConstant.TRUE)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("dept.notexist"));
        }
        return dept;
    }

    @Override
    public List<Dept> queryDeptExcludeChild(String id) {
        QueryWrapper<Dept> wrapper = new QueryWrapper();
        wrapper.lambda().eq(Dept::getDeleted,BaseConstant.FALSE);
        //id为空认为是点击的添加按钮
        if (StrUtil.isEmpty(id)){
            return list(wrapper);
        }else {
            //查看该部门下的所有子部门
            List<String> excludeIds = getAllChildren(Arrays.asList(id));
            excludeIds.add(id);
            if (CollUtil.isNotEmpty(excludeIds)){
                wrapper.lambda().notIn(Dept::getId,excludeIds);
            }
            return list(wrapper);
        }
    }

    @Override
    public TreeSelect getDeptTree() {
        //获取顶级节点
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Dept::getDeleted,BaseConstant.FALSE);
        wrapper.lambda().eq(Dept::getParentId,BaseConstant.TOPNODE);
        List<Dept> toplist =  list(wrapper);
        if (CollUtil.isEmpty(toplist) || toplist.size() > 1){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("dept.topNodeError"));
        }
        Dept topDept = toplist.get(0);
        buildDeptTree(topDept);
        return new TreeSelect(topDept);
    }

    //递归，建立子树形结构
    public void buildDeptTree(Dept pNode){
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Dept::getDeleted,BaseConstant.FALSE);
        wrapper.lambda().eq(Dept::getParentId,pNode.getId());
        List<Dept> chilDepts = list(wrapper);
        if (CollUtil.isNotEmpty(chilDepts)){
            for(Dept deptNode : chilDepts) {
                buildDeptTree(deptNode);
            }
        }
        pNode.setChildren(chilDepts);
    }

    //递归查询出该部门的所有子节点
    public List<String> getAllChildren(List<String> parentIds){
        QueryWrapper<Dept> wrapper = new QueryWrapper<>();
        wrapper.lambda().select(Dept::getId);
        wrapper.lambda().eq(Dept::getDeleted,BaseConstant.FALSE);
        wrapper.lambda().in(Dept::getParentId,parentIds);
        List<String> ids = list(wrapper).stream().map(Dept::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(ids)){
            ids.addAll(getAllChildren(ids));
            return ids;
        }else {
            return new ArrayList<>();
        }
    }

    private boolean updateFlag (Dept newDept, Dept oldDept){
        StringBuffer sb1 = new StringBuffer("");
        StringBuffer sb2 = new StringBuffer("");
        sb1.append(newDept.getDeptName());
        sb2.append(oldDept.getDeptName());
        sb1.append(newDept.getParentId());
        sb2.append(oldDept.getParentId());
        sb1.append(newDept.getOrderNum());
        sb2.append(oldDept.getOrderNum());
        return sb1.toString().equals(sb2.toString());

    }

    private boolean checkFiled(Dept dept){
        if(ObjectUtil.isEmpty(dept) ||
                StrUtil.hasEmpty(dept.getDeptName(),dept.getParentId()) ||
                ObjectUtil.isEmpty(dept.getOrderNum())
        ){
            return true;
        }
        return false;
    }
}
