package com.ws.base.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("dict_data")
public class DictData extends BaseModel implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String dictCode;

    private String dictName;

    private String dictTypeId;

    private Integer orderNum;

    private String remark;
}
