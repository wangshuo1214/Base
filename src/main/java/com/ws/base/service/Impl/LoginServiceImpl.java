package com.ws.base.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ws.base.constant.BaseConstant;
import com.ws.base.constant.HttpStatus;
import com.ws.base.entity.LoginBody;
import com.ws.base.exception.BaseException;
import com.ws.base.exception.UserException;
import com.ws.base.mapper.RoleMenuMapper;
import com.ws.base.mapper.UserRoleMapper;
import com.ws.base.model.Menu;
import com.ws.base.model.User;
import com.ws.base.service.ILoginService;
import com.ws.base.service.IMenuService;
import com.ws.base.service.IUserService;
import com.ws.base.util.JwtTokenUtil;
import com.ws.base.util.MessageUtil;
import com.ws.base.util.PasswordUtil;
import com.ws.base.util.RedisUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private IMenuService iMenuService;
    @Override
    public String login(LoginBody loginBody) {
        if (loginBody == null || StrUtil.hasEmpty(loginBody.getUsername(),loginBody.getPassword())){
            throw new BaseException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("paramsError"));
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getUserName,loginBody.getUsername());
        wrapper.lambda().eq(User::getDeleted, BaseConstant.FALSE);
        User user = iUserService.getOne(wrapper);
        //用户不存在
        if (user == null){
            throw new UserException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("user.notexist"));
        }
        //密码不正确
        if (!StrUtil.equals(PasswordUtil.pwdEncrypt(loginBody.getPassword()),user.getPassword())){
            throw new UserException(HttpStatus.BAD_REQUEST, MessageUtil.getMessage("user.passwordError"));
        }
        //用户状态异常（停用）
        if (StrUtil.equals(BaseConstant.EXCEPTION,user.getStatus())){
            throw new UserException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("user.statusError"));
        }

        //生成Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId",user.getId());
        String token = jwtTokenUtil.createToken(claims);

        //Token放在缓存里,用户每次请求的时候使用
        redisUtil.setCacheObject("userToken:"+user.getId(),token,5, TimeUnit.HOURS);

        return token;
    }

    @Override
    public User getUserInfoByToken(String token) {
        if (StrUtil.isEmpty(token)){
            throw new UserException(HttpStatus.BAD_REQUEST,MessageUtil.getMessage("paramsError"));
        }

        //token解析结果
        Claims claims = null;
        try {
            claims = jwtTokenUtil.getClaimsFromToken(token);
            //用户id
            String userId = (String) claims.get("userId");
            User user = iUserService.getById(userId);
            //用户关联角色ids
            List<String> roleIds = userRoleMapper.queryRoleIdsByUserId(userId);
            user.setRoles(roleIds);
            if (ObjectUtil.isNotEmpty(user)){
                return user;
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Boolean logout(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");

        //token解析结果
        Claims claims = null;
        try {
            claims = jwtTokenUtil.getClaimsFromToken(token);
            String userId = (String) claims.get("userId");
            //删除缓存的token
            redisUtil.deleteObject("userToken:"+userId);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
