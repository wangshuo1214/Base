package com.ws.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ws.base.model.Dept;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeptMapper extends BaseMapper<Dept> {
}
