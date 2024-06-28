package com.ws.base.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("role_menu")
public class RoleMenu extends BaseModel implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String roleId;

    private String menuId;
}
