
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `code` varchar(255) ,
  `name` varchar(255) 
);


DROP TABLE IF EXISTS `multi_table_relation`;
CREATE TABLE `multi_table_relation`  (
  `code` varchar(128)  COMMENT '唯一编号,推荐使用 tableName1__tableName2',
  `class1` varchar(255)  COMMENT '对应实体类',
  `class2` varchar(255)  COMMENT '对应实体类',
  `table_name1` varchar(128)  COMMENT '两张表名',
  `table_name2` varchar(128)  COMMENT '两张表名',
  `class1_one_or_many` varchar(8)  COMMENT '一对一 一对多 多对一 多对多 ONE-一 MANY-多',
  `class2_one_or_many` varchar(8)  COMMENT '一对一 一对多 多对一 多对多 ONE-一 MANY-多',
  `class1_require` tinyint(2) NULL DEFAULT NULL COMMENT '关系中 是否是否,表1一定该有数据/表2一定该有数据',
  `class2_require` tinyint(2) NULL DEFAULT NULL COMMENT '关系中 是否是否,表1一定该有数据/表2一定该有数据',
  `class1_key_prop` varchar(128)  COMMENT '两个表关联的字段',
  `class2_key_prop` varchar(128)  COMMENT '两个表关联的字段',
  PRIMARY KEY (`code`)
);


DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `username` varchar(255) ,
  `sex` varchar(255) ,
  `parent_id` int(11) NULL DEFAULT NULL,
  `balance` decimal(10, 2) NULL DEFAULT NULL,
  `numbers` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);


DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` int(11) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `province` varchar(255) ,
  `user_id` int(11) NULL DEFAULT NULL,
  `street` varchar(255) ,
  `street_code` varchar(255) ,
  PRIMARY KEY (`id`)
);


DROP TABLE IF EXISTS `user_staff`;
CREATE TABLE `user_staff`  (
  `id` int(11) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `staff_name` varchar(255) ,
  `sex` varchar(255) ,
  `admin_user_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
);