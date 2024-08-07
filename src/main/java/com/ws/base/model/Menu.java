package com.ws.base.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("menu")
public class Menu extends BaseModel implements Serializable {


    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String menuName;

    private String parentId;

    private Integer orderNum;

    private String path;

    private String component;

    private String query;

    private String isFrame;

    private String isCache;

    private String menuType;

    private String visible;

//    private String perms;

    private String icon;

    private String remark;

    /** 子菜单 */
    @TableField(exist = false)
    private List<Menu> children;
}
