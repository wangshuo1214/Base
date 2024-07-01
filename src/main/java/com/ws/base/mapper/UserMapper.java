package com.ws.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ws.base.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
