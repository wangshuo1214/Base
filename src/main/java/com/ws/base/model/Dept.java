package com.ws.base.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@TableName("dept")
public class Dept extends BaseModel implements Serializable {


    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String deptName;

    private String parentId;

    private Integer orderNum;

    private String leader;

    private String phone;

    /** 子菜单 */
    @TableField(exist = false)
    private List<Dept> children;
}
