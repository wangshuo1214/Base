package com.ws.base.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("role")
public class Role extends BaseModel implements Serializable {


    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String roleName;

    private String roleKey;

    private Integer orderNum;

    private Boolean menuCheckStrictly;

    private String remark;

    @TableField(exist=false)
    private List<String> menuIds;

    @TableField(exist=false)
    private List<String> userIds;
}
