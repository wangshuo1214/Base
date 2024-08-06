INSERT INTO dept
(id, dept_name, parent_id, order_num, create_time, update_time, deleted)
VALUES('40126809-76ff-4041-a224-48889a76c6e7', '研发部', '6f7b99c3-9889-4e0b-a720-98adbef7b093', 1, '2024-08-06 14:42:59', '2024-08-06 14:42:59', '0');
INSERT INTO dept
(id, dept_name, parent_id, order_num, create_time, update_time, deleted)
VALUES('6f7b99c3-9889-4e0b-a720-98adbef7b093', '总部', '0', 0, '2024-08-06 14:28:34', '2024-08-06 14:28:34', '0');



INSERT INTO menu
(id, menu_name, parent_id, order_num, `path`, component, query, is_frame, is_cache, menu_type, visible, perms, icon, remark, create_time, update_time, deleted)
VALUES('0614133b-c236-4efd-b2c1-a115fefa22c0', '部门管理', '0', 1, 'dept', 'dept/index', NULL, '0', 0, 'M', '1', 'dept:list', 'tree copy', NULL, '2023-01-17 20:48:31', '2023-01-17 20:48:31', '0');
INSERT INTO menu
(id, menu_name, parent_id, order_num, `path`, component, query, is_frame, is_cache, menu_type, visible, perms, icon, remark, create_time, update_time, deleted)
VALUES('287c8e95-4ddf-4d7f-bc5f-b478342027ac', '用户管理', '0', 2, 'user', 'user/index', NULL, '0', 0, 'M', '1', 'user:list', 'user', NULL, '2023-01-17 20:48:31', '2023-01-17 20:48:31', '0');
INSERT INTO menu
(id, menu_name, parent_id, order_num, `path`, component, query, is_frame, is_cache, menu_type, visible, perms, icon, remark, create_time, update_time, deleted)
VALUES('6beaae92-c47c-405f-85c2-02dbce192289', '角色管理', '0', 3, 'role', 'role/index', NULL, '0', 0, 'M', '1', 'role:list', 'peoples', NULL, '2023-01-17 20:48:31', '2023-01-17 20:48:31', '0');
INSERT INTO menu
(id, menu_name, parent_id, order_num, `path`, component, query, is_frame, is_cache, menu_type, visible, perms, icon, remark, create_time, update_time, deleted)
VALUES('77e6ea57-0830-411a-96c0-01c872d743d7', '菜单管理', '0', 4, 'menu', 'menu/index', NULL, '0', 0, 'M', '1', 'menu:list', 'tree-table', NULL, '2023-01-17 20:48:31', '2023-01-17 20:48:31', '0');
INSERT INTO menu
(id, menu_name, parent_id, order_num, `path`, component, query, is_frame, is_cache, menu_type, visible, perms, icon, remark, create_time, update_time, deleted)
VALUES('eeeb897d-1212-4a48-ba5c-cceb69e0c44b', '字典管理', '0', 5, 'dict', 'dict/index', NULL, '0', 0, 'M', '1', 'dict:list', 'dict', NULL, '2023-01-17 20:48:31', '2023-01-17 20:48:31', '0');


INSERT INTO `role`
(id, role_name, role_key, order_num, menu_check_strictly, remark, create_time, update_time, deleted)
VALUES('3eac9515-4430-40a5-a55e-0f83ddaf0ac9', '超级管理员', 'admin', 1, 1, NULL, '2022-12-11 16:17:54', '2022-12-11 16:17:54', '0');


INSERT INTO role_menu
(id, role_id, menu_id, create_time, update_time, deleted)
VALUES('0a4add52-5460-4697-b601-ba5bfcfa6d05', '3eac9515-4430-40a5-a55e-0f83ddaf0ac9', '0614133b-c236-4efd-b2c1-a115fefa22c0', '2023-01-17 20:48:31', '2023-01-17 20:48:31', '0');
INSERT INTO role_menu
(id, role_id, menu_id, create_time, update_time, deleted)
VALUES('4f72fbc3-fd60-40a6-beca-7ea9fcdc4d0a', '3eac9515-4430-40a5-a55e-0f83ddaf0ac9', '287c8e95-4ddf-4d7f-bc5f-b478342027ac', '2023-01-17 20:48:31', '2023-01-17 20:48:31', '0');
INSERT INTO role_menu
(id, role_id, menu_id, create_time, update_time, deleted)
VALUES('5e1cd1f3-2fe2-4ff8-ace8-5ccc132e292e', '3eac9515-4430-40a5-a55e-0f83ddaf0ac9', '6beaae92-c47c-405f-85c2-02dbce192289', '2023-01-17 20:48:31', '2023-01-17 20:48:31', '0');
INSERT INTO role_menu
(id, role_id, menu_id, create_time, update_time, deleted)
VALUES('636b3e2f-4520-4763-a3b9-35be15ed10a4', '3eac9515-4430-40a5-a55e-0f83ddaf0ac9', '77e6ea57-0830-411a-96c0-01c872d743d7', '2023-01-17 20:48:31', '2023-01-17 20:48:31', '0');
INSERT INTO role_menu
(id, role_id, menu_id, create_time, update_time, deleted)
VALUES('6cd9d9cd-c1f3-4140-9389-6ac397f12de9', '3eac9515-4430-40a5-a55e-0f83ddaf0ac9', 'eeeb897d-1212-4a48-ba5c-cceb69e0c44b', '2023-01-17 20:48:31', '2023-01-17 20:48:31', '0');


INSERT INTO `user`
(id, dept_id, user_name, password, real_name, avatar, status, remark, create_time, update_time, deleted)
VALUES('69f08f29-bc29-4f9c-bd6c-6a995fc946fd', '40126809-76ff-4041-a224-48889a76c6e7', 'wangshuo', 'd8bfb0ed576a77b47458907b1f10d463205475ee64ea4f81cc337367beed36b9', '王烁', NULL, '1', NULL, '2024-08-06 14:47:32', '2024-08-06 14:47:32', '0');

INSERT INTO user_role
(id, user_id, role_id, create_time, update_time, deleted)
VALUES('6f7b99c3-9889-4e0b-a720-98adbef7b12d', '69f08f29-bc29-4f9c-bd6c-6a995fc946fd', '3eac9515-4430-40a5-a55e-0f83ddaf0ac9', '2022-12-11 16:17:54', '2022-12-11 16:17:54', '0');




INSERT INTO dict_type
(id, dict_type, dict_name, order_num, remark, create_time, update_time, deleted)
VALUES('85c0bca2-c47b-4769-bc09-7f265e42ad9b', 'user_status', '用户状态', 1, NULL, '2022-11-13 11:24:56', '2022-11-13 11:24:56', '0');

INSERT INTO dict_data
(id, dict_code, dict_name, dict_type_id, order_num, remark, create_time, update_time, deleted)
VALUES('27cff09d-aa78-41c4-a15d-dcea2bc74977', '0', '停用', '85c0bca2-c47b-4769-bc09-7f265e42ad9b', 2, NULL, '2022-11-13 11:24:56', '2022-11-13 11:24:56', '0');
INSERT INTO dict_data
(id, dict_code, dict_name, dict_type_id, order_num, remark, create_time, update_time, deleted)
VALUES('78dde12c-8d7c-48dc-848a-b9672dce809d', '1', '正常', '85c0bca2-c47b-4769-bc09-7f265e42ad9b', 1, NULL, '2022-11-13 11:24:56', '2022-11-13 11:24:56', '0');
