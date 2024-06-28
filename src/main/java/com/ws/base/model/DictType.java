package com.ws.base.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("dict_type")
public class DictType extends BaseModel implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String dictType;

    private String dictName;

    private Integer orderNum;

    private String remark;
}
