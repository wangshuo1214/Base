package com.ws.base.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("user")
public class User extends BaseModel implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String deptId;

    @TableField(exist = false)
    private String deptName;

    private String userName;

    private String password;

    private String realName;

    private String avatar;

    private String status;

    private String remark;

    //用于角色页面授权用户使用
    @TableField(exist = false)
    private String roleId;

    @TableField(exist = false)
    private String roleName;

    @TableField(exist = false)
    private List<String> permissions;

    @TableField(exist = false)
    private List<String> roles;
}
