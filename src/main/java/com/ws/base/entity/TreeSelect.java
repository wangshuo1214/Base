package com.ws.base.entity;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ws.base.model.Dept;
import com.ws.base.model.Menu;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TreeSelect {

    /** 节点ID */
    private String id;

    /** 节点名称 */
    private String label;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelect> children;

    public TreeSelect(Menu menu) {
        this.id = menu.getId();
        this.label = menu.getMenuName();
        if (CollUtil.isNotEmpty(menu.getChildren())) {
            this.children = menu.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
        }
    }

    public TreeSelect(Dept dept) {
        this.id = dept.getId();
        this.label = dept.getDeptName();
        if (CollUtil.isNotEmpty(dept.getChildren())) {
            this.children = dept.getChildren().stream().map(TreeSelect::new).collect(Collectors.toList());
        }
    }
    public TreeSelect(){

    }
}
