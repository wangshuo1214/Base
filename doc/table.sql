-- base.dept definition

CREATE TABLE `dept` (
                        `id` varchar(36) NOT NULL COMMENT '部门id',
                        `dept_name` varchar(50) NOT NULL COMMENT '部门名称',
                        `parent_id` varchar(36) NOT NULL COMMENT '父节点',
                        `order_num` int(11) NOT NULL COMMENT '序号',
                        `create_time` datetime NOT NULL COMMENT '创建时间',
                        `update_time` datetime NOT NULL COMMENT '修改时间',
                        `deleted` char(1) NOT NULL COMMENT '删除标志',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- base.dict_data definition

CREATE TABLE `dict_data` (
                             `id` varchar(36) NOT NULL COMMENT '表主键',
                             `dict_code` varchar(50) NOT NULL COMMENT '字典编码',
                             `dict_name` varchar(50) NOT NULL COMMENT '字典名称',
                             `dict_type_id` varchar(36) NOT NULL COMMENT '父类型',
                             `order_num` int(11) NOT NULL COMMENT '序号',
                             `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
                             `create_time` datetime NOT NULL COMMENT '创建时间',
                             `update_time` datetime NOT NULL COMMENT '修改时间',
                             `deleted` char(1) NOT NULL COMMENT '删除标志',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- base.dict_type definition

CREATE TABLE `dict_type` (
                             `id` varchar(36) NOT NULL COMMENT '表主键',
                             `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
                             `dict_name` varchar(100) NOT NULL COMMENT '字典名称',
                             `order_num` int(11) NOT NULL COMMENT '序号',
                             `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
                             `create_time` datetime NOT NULL COMMENT '创建时间',
                             `update_time` datetime NOT NULL COMMENT '修改时间',
                             `deleted` char(1) NOT NULL COMMENT '删除标志',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- base.menu definition

CREATE TABLE `menu` (
                        `id` varchar(36) NOT NULL COMMENT '菜单id',
                        `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
                        `parent_id` varchar(36) NOT NULL COMMENT '父节点',
                        `order_num` int(11) NOT NULL COMMENT '序号',
                        `path` varchar(100) DEFAULT NULL COMMENT '路由地址',
                        `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
                        `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
                        `is_frame` char(1) NOT NULL COMMENT '是否为外链',
                        `is_cache` int(11) DEFAULT '1' COMMENT '是否缓存',
                        `menu_type` char(1) NOT NULL COMMENT '菜单类型（M目录 C菜单 F按钮）',
                        `visible` char(1) NOT NULL COMMENT '显示标志',
                        `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
                        `icon` varchar(255) DEFAULT NULL COMMENT '菜单图标',
                        `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
                        `create_time` datetime NOT NULL COMMENT '创建时间',
                        `update_time` datetime NOT NULL COMMENT '修改时间',
                        `deleted` char(1) NOT NULL COMMENT '删除标志',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- base.`role` definition

CREATE TABLE `role` (
                        `id` varchar(36) NOT NULL COMMENT '角色id',
                        `role_name` varchar(100) NOT NULL COMMENT '角色名称',
                        `role_key` varchar(100) NOT NULL COMMENT '角色编码',
                        `order_num` int(11) NOT NULL COMMENT '序号',
                        `menu_check_strictly` tinyint(1) NOT NULL COMMENT '菜单树选择项是否关联显示',
                        `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
                        `create_time` datetime NOT NULL COMMENT '创建日期',
                        `update_time` datetime NOT NULL COMMENT '修改日期',
                        `deleted` char(1) NOT NULL COMMENT '删除标志',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- base.role_menu definition

CREATE TABLE `role_menu` (
                             `id` varchar(36) NOT NULL COMMENT '主键',
                             `role_id` varchar(36) NOT NULL COMMENT '角色id',
                             `menu_id` varchar(36) NOT NULL COMMENT '菜单id',
                             `create_time` datetime NOT NULL COMMENT '创建时间',
                             `update_time` datetime NOT NULL COMMENT '修改时间',
                             `deleted` char(1) NOT NULL COMMENT '删除标志',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- base.`user` definition

CREATE TABLE `user` (
                        `id` varchar(36) NOT NULL COMMENT '表主键',
                        `dept_id` varchar(36) NOT NULL COMMENT '部门id',
                        `user_name` varchar(36) NOT NULL COMMENT '用户账号',
                        `password` varchar(100) NOT NULL COMMENT '密码',
                        `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
                        `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
                        `status` char(1) NOT NULL COMMENT '状态（0停用，1正常）',
                        `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
                        `create_time` datetime NOT NULL COMMENT '创建时间',
                        `update_time` datetime NOT NULL COMMENT '修改时间',
                        `deleted` char(1) NOT NULL COMMENT '删除标志',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- base.user_role definition

CREATE TABLE `user_role` (
                             `id` varchar(36) NOT NULL COMMENT '主键',
                             `user_id` varchar(36) NOT NULL COMMENT '用户id',
                             `role_id` varchar(36) NOT NULL COMMENT '角色id',
                             `create_time` datetime NOT NULL COMMENT '创建时间',
                             `update_time` datetime NOT NULL COMMENT '修改时间',
                             `deleted` char(1) NOT NULL COMMENT '删除标志',
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;