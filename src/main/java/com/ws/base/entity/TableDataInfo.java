package com.ws.base.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TableDataInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<?> rows;
}

