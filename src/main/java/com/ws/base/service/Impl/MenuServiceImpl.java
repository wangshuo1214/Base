package com.ws.base.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ws.base.constant.BaseConstant;
import com.ws.base.constant.HttpStatus;
import com.ws.base.entity.MetaVo;
import com.ws.base.entity.RouterVo;
import com.ws.base.entity.TreeSelect;
import com.ws.base.exception.BaseException;
import com.ws.base.mapper.MenuMapper;
import com.ws.base.mapper.RoleMenuMapper;
import com.ws.base.mapper.UserRoleMapper;
import com.ws.base.model.Menu;
import com.ws.base.model.User;
import com.ws.base.service.IMenuService;
import com.ws.base.util.InitFieldUtil;
import com.ws.base.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl  extends ServiceImpl<MenuMapper, Menu> implements IMenuService{

    @Autowired
    RoleMenuMapper roleMenuMapper;

    @Autowired
    UserRoleMapper userRoleMapper;
    @Override
    public boolean addMenu(Menu menu) {
        if (checkField(menu)){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        //父菜单
        Menu parentMenu = getById(menu.getParentId());

        //如果不在顶级创建 则需要判断父节点状态
        if ( !StrUtil.equals(menu.getParentId(), BaseConstant.FALSE) && (ObjectUtil.isEmpty(parentMenu) || StrUtil.equals(parentMenu.getDeleted(),BaseConstant.TRUE))){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("parentStatusError"));
        }

        //判断父节点下菜单是否重复
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Menu::getParentId,menu.getParentId());
        wrapper.lambda().eq(Menu::getMenuName,menu.getMenuName().trim());
        wrapper.lambda().eq(Menu::getDeleted,BaseConstant.FALSE);
        List<Menu> repeatMenus = list(wrapper);
        if (CollUtil.isNotEmpty(repeatMenus)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("menu.nameRepeat"));
        }

        //初始化基本属性
        if (!InitFieldUtil.initField(menu)){
            throw new BaseException(HttpStatus.ERROR,MessageUtil.getMessage("initFieldError"));
        }

        menu.setId(UUID.randomUUID().toString());
        return save(menu);
    }

    @Override
    public List<Menu> queryMenu(Menu menu) {
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Menu::getDeleted,BaseConstant.FALSE);
        if (StrUtil.isNotEmpty(menu.getMenuName())){
            wrapper.lambda().like(Menu::getMenuName,menu.getMenuName().trim());
        }
        wrapper.lambda().orderByAsc(Menu::getOrderNum).orderByDesc(Menu::getUpdateTime);
        return list(wrapper);
    }

    @Override
    public Menu getMenu(String id) {
        if (StrUtil.isEmpty(id)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }
        Menu menu = getById(id);
        if (ObjectUtil.isEmpty(menu) || StrUtil.equals(menu.getDeleted(),BaseConstant.TRUE)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("menu.notexist"));
        }
        return menu;
    }

    @Override
    public boolean updMenu(Menu newMenu) {
        if (checkField(newMenu)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }
        //父菜单
        Menu parentMenu = getById(newMenu.getParentId());

        //判断父节点状态
        if ( !StrUtil.equals(newMenu.getParentId(), BaseConstant.FALSE) && (ObjectUtil.isEmpty(parentMenu) || StrUtil.equals(parentMenu.getDeleted(),BaseConstant.TRUE))){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("parentStatusError"));
        }

        //判断父节点下菜单是否重复
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Menu::getParentId,newMenu.getParentId());
        wrapper.lambda().eq(Menu::getMenuName,newMenu.getMenuName().trim());
        wrapper.lambda().eq(Menu::getDeleted,BaseConstant.FALSE);
        wrapper.lambda().ne(Menu::getId,newMenu.getId());
        List<Menu> repeatMenus = list(wrapper);
        if (CollUtil.isNotEmpty(repeatMenus)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("menu.nameRepeat"));
        }

        Menu oldMenu = getById(newMenu.getId());
        if (!updateFlag(newMenu,oldMenu)){
            oldMenu.setMenuName(newMenu.getMenuName());
            oldMenu.setParentId(newMenu.getParentId());
            oldMenu.setMenuType(newMenu.getMenuType());
            oldMenu.setIcon(newMenu.getIcon());
            oldMenu.setOrderNum(newMenu.getOrderNum());
            oldMenu.setIsFrame(newMenu.getIsFrame());
            oldMenu.setPath(newMenu.getPath());
            oldMenu.setComponent(newMenu.getComponent());
            oldMenu.setPerms(newMenu.getPerms());
            oldMenu.setQuery(newMenu.getQuery());
            oldMenu.setIsCache(newMenu.getIsCache());
            oldMenu.setVisible(newMenu.getVisible());
            oldMenu.setUpdateTime(new Date());
        }

        return updateById(oldMenu);
    }

    @Override
    @Transactional
    public boolean deleteMenu(String id) {
        if (StrUtil.isEmpty(id)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }

        //有子节点不能删除
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Menu::getParentId,id);
        wrapper.lambda().eq(Menu::getDeleted,BaseConstant.FALSE);
        List<Menu> childList = list(wrapper);
        if (CollUtil.isNotEmpty(childList)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("menu.hasChildren"));
        }
        //删除角色和菜单的关联关系
        roleMenuMapper.deleteRoleMenuByMenuId(id);

        Menu menu = getById(id);
        menu.setDeleted(BaseConstant.TRUE);
        menu.setUpdateTime(new Date());

        return updateById(menu);
    }

    @Override
    public List<Menu> queryMenuExcludeChild(String id) {
        QueryWrapper<Menu> wrapper = new QueryWrapper();
        wrapper.lambda().eq(Menu::getDeleted,BaseConstant.FALSE);
        wrapper.lambda().orderByAsc(Menu::getOrderNum).orderByDesc(Menu::getUpdateTime);
        //id为空认为是点击的添加按钮
        if (StrUtil.isEmpty(id)){
            return list(wrapper);
        }else {
            List<String> excludeIds = getChildren(Arrays.asList(id));
            if (CollUtil.isNotEmpty(excludeIds)){
                wrapper.lambda().notIn(Menu::getId,excludeIds);
            }
            return list(wrapper);
        }
    }

    @Override
    public List<Menu> buildMenuTree(List<Menu> menus) {
        List<Menu> returnList = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        for (Menu menu : menus) {
            tempList.add(menu.getId());
        }
        for (Iterator<Menu> iterator = menus.iterator(); iterator.hasNext();) {
            Menu menu = (Menu) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<Menu> menus) {
        List<Menu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    @Override
    public List<String> selectMenuListByRoleId(String roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public List<RouterVo> queryMenuTreeByUserId(User user) {
        if (ObjectUtil.isEmpty(user)){
            throw new BaseException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }
        List<String> roleIds = userRoleMapper.queryRoleIdsByUserId(user.getId());
        if (CollUtil.isNotEmpty(roleIds)){
            List<String> menuIds = roleMenuMapper.selectMenuIdsByRoleIds(roleIds);
            if (CollUtil.isNotEmpty(menuIds)){
                QueryWrapper<Menu> wrapper = new QueryWrapper<>();
                wrapper.lambda().in(Menu::getId,menuIds);
                wrapper.lambda().orderByAsc(Menu::getOrderNum).orderByDesc(Menu::getUpdateTime);
                List<Menu> menus = list(wrapper);
                menus.stream().filter(menu -> menu.getMenuType() != "B").collect(Collectors.toList());
                if (CollUtil.isNotEmpty(menus)){
                    return buildMenus(buildMenuTree(menus));
                }
            }
        }
        return new ArrayList<>();
    }

    private void recursionFn(List<Menu> list, Menu t) {
        // 得到子节点列表
        List<Menu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (Menu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<Menu> getChildList(List<Menu> list, Menu t) {
        List<Menu> tlist = new ArrayList<>();
        Iterator<Menu> it = list.iterator();
        while (it.hasNext()) {
            Menu n = (Menu) it.next();
            if (StrUtil.equals(n.getParentId(),t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<Menu> list, Menu t) {
        return getChildList(list, t).size() > 0;
    }

    private List<String> getChildren(List<String> parentIds){
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.lambda().select(Menu::getId);
        wrapper.lambda().eq(Menu::getDeleted,BaseConstant.FALSE);
        wrapper.lambda().in(Menu::getParentId,parentIds);
        wrapper.lambda().orderByAsc(Menu::getOrderNum).orderByDesc(Menu::getUpdateTime);
        List<String> ids = list(wrapper).stream().map(Menu::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(ids)){
            ids.addAll(getChildren(ids));
            return ids;
        }else {
            return new ArrayList<>();
        }
    }

    private boolean updateFlag(Menu newMenu, Menu oldMenu){

        StringBuffer sb1 = new StringBuffer("");
        StringBuffer sb2 = new StringBuffer("");

        sb1.append(newMenu.getMenuName());
        sb2.append(oldMenu.getMenuName());
        sb1.append(newMenu.getParentId());
        sb2.append(oldMenu.getParentId());
        sb1.append(newMenu.getMenuType());
        sb2.append(oldMenu.getMenuType());
        sb1.append(newMenu.getIcon());
        sb2.append(oldMenu.getIcon());
        sb1.append(newMenu.getOrderNum());
        sb2.append(oldMenu.getOrderNum());
        sb1.append(newMenu.getIsFrame());
        sb2.append(oldMenu.getIsFrame());
        sb1.append(newMenu.getPath());
        sb2.append(oldMenu.getPath());
        sb1.append(newMenu.getComponent());
        sb2.append(oldMenu.getComponent());
        sb1.append(newMenu.getPerms());
        sb2.append(oldMenu.getPerms());
        sb1.append(newMenu.getQuery());
        sb2.append(oldMenu.getQuery());
        sb1.append(newMenu.getIsCache());
        sb2.append(oldMenu.getIsCache());
        sb1.append(newMenu.getVisible());
        sb2.append(oldMenu.getVisible());

        return sb1.toString().equals(sb2.toString());
    }

    private boolean checkField(Menu menu){

        //非空校验
        if (ObjectUtil.isEmpty(menu) || StrUtil.isEmpty(menu.getMenuType()) ||
                ((StrUtil.equals(menu.getMenuType(), BaseConstant.CATALOGUE ) || StrUtil.equals(menu.getMenuType(), BaseConstant.MENU)) && (StrUtil.hasEmpty(menu.getMenuName(),menu.getPath(),menu.getIsFrame(),menu.getVisible(),menu.getParentId()) || ObjectUtil.isEmpty(menu.getOrderNum()))) ||
                (StrUtil.equals(menu.getMenuType(), BaseConstant.BUTTON) && (StrUtil.hasEmpty(menu.getMenuName(),menu.getParentId()) || ObjectUtil.isEmpty(menu.getOrderNum())))
        ){
            return true;
        }
        //对菜单类型进行校验,菜单类型只能是 C M B
        if (!StrUtil.equalsAny(menu.getMenuType(),BaseConstant.CATALOGUE,BaseConstant.MENU,BaseConstant.BUTTON)){
            return true;
        }
        //选了目录、菜单、按钮其中之一后，有的选项必须为空，此处进行校验
        if ((StrUtil.equals(menu.getMenuType(), BaseConstant.CATALOGUE) && !StrUtil.hasEmpty(menu.getPath(),menu.getPerms(),menu.getQuery(),menu.getIsCache())) ||
                (StrUtil.equals(menu.getMenuType(), BaseConstant.BUTTON) && !StrUtil.hasEmpty(menu.getIcon(),menu.getIsFrame(),menu.getPath(),menu.getVisible(),menu.getComponent(),menu.getQuery(),menu.getIsCache()))
        ){
            return true;
        }
        return false;
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    public List<RouterVo> buildMenus(List<Menu> menus) {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (Menu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(BaseConstant.FALSE.equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StrUtil.equals("1", menu.getIsCache()), menu.getPath()));
            List<Menu> cMenus = menu.getChildren();
            if (!cMenus.isEmpty() && cMenus.size() > 0 && BaseConstant.CATALOGUE.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache())));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(Menu menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为菜单）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }
    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(Menu menu) {
        String routerPath = menu.getPath();
        // 非外链并且是一级目录（类型为目录）
        if ( BaseConstant.TOPNODE.equals(menu.getParentId()) && BaseConstant.CATALOGUE.equals(menu.getMenuType())
                && BaseConstant.FALSE.equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }
    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(Menu menu)
    {
        String component = BaseConstant.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = BaseConstant.PARENT_VIEW;
        }
        return component;
    }
    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(Menu menu) {
        return !(BaseConstant.TOPNODE.equals(menu.getParentId())) && BaseConstant.CATALOGUE.equals(menu.getMenuType());
    }
    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(Menu menu) {
        return menu.getParentId().equals(BaseConstant.TOPNODE)&& BaseConstant.MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(BaseConstant.FALSE);
    }
}
