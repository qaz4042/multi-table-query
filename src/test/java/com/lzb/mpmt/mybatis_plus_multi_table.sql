/*
 Navicat Premium Data Transfer

 Source Server         : 0_localhost
 Source Server Type    : MySQL
 Source Server Version : 50734
 Source Host           : localhost:3306
 Source Schema         : mybatis_plus_multi_table

 Target Server Type    : MySQL
 Target Server Version : 50734
 File Encoding         : 65001

 Date: 12/11/2021 16:02:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for multi_table_relation
-- ----------------------------
DROP TABLE IF EXISTS `multi_table_relation`;
CREATE TABLE `multi_table_relation`  (
  `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '唯一编号,推荐使用 tableName1__tableName2',
  `class1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对应实体类',
  `class2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对应实体类',
  `table_name1` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '两张表名',
  `table_name2` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '两张表名',
  `class1_one_or_many` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '一对一 一对多 多对一 多对多 ONE-一 MANY-多',
  `class2_one_or_many` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '一对一 一对多 多对一 多对多 ONE-一 MANY-多',
  `class1_require` tinyint(2) NULL DEFAULT NULL COMMENT '关系中 是否是否,表1一定该有数据/表2一定该有数据',
  `class2_require` tinyint(2) NULL DEFAULT NULL COMMENT '关系中 是否是否,表1一定该有数据/表2一定该有数据',
  `class1_key_prop` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '两个表关联的字段',
  `class2_key_prop` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '两个表关联的字段',
  PRIMARY KEY (`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '类和类的关系' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of multi_table_relation
-- ----------------------------
INSERT INTO `multi_table_relation` VALUES ('user__user_address', 'com.lzb.mpmt.test.model.User', 'com.lzb.mpmt.test.model.UserAddress', 'user', 'user_address', 'ONE', 'MANY', 1, 0, 'id', 'user_id');
INSERT INTO `multi_table_relation` VALUES ('user__user_staff', 'com.lzb.mpmt.test.model.User', 'com.lzb.mpmt.test.model.UserStaff', 'user', 'user_staff', 'ONE', 'MANY', 1, 0, 'id', 'admin_user_id');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `parent_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'username1', '1', 1);
INSERT INTO `user` VALUES (2, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'username2', '1', 2);

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` int(11) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `user_id` int(11) NULL DEFAULT NULL,
  `street` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (111, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'province1', 1, 'street1');
INSERT INTO `user_address` VALUES (112, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'province2', 1, 'street2');
INSERT INTO `user_address` VALUES (113, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'province3', 1, 'street3');
INSERT INTO `user_address` VALUES (114, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'province4', 2, 'street4');

-- ----------------------------
-- Table structure for user_staff
-- ----------------------------
DROP TABLE IF EXISTS `user_staff`;
CREATE TABLE `user_staff`  (
  `id` int(11) NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `staff_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `admin_user_id` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of user_staff
-- ----------------------------
INSERT INTO `user_staff` VALUES (11, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'staff1', '0', 1);
INSERT INTO `user_staff` VALUES (12, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'staff2', '0', 1);
INSERT INTO `user_staff` VALUES (13, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'staff3', '0', 2);

SET FOREIGN_KEY_CHECKS = 1;
