package com.ws.base.entity;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class PageDomain {
    /** 当前记录起始索引 */
    private Integer pageNum;

    /** 每页显示记录数 */
    private Integer pageSize;

    /** 排序列 */
    private String orderByColumn;

    /** 排序的方向desc或者asc */
    private String sortFlag = "asc";

    public String getOrderBy() {
        if (StrUtil.isEmpty(orderByColumn)) {
            return "";
        }
        //驼峰转下划线命名
        return StrUtil.toUnderlineCase(orderByColumn) + " " + sortFlag;
    }
}
