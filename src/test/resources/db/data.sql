INSERT INTO `address` VALUES ('1001', '1001name');
INSERT INTO `address` VALUES ('1002', '1001name');
INSERT INTO `address` VALUES ('1003', '1001name');


INSERT INTO `multi_table_relation` VALUES ('user__user_address', 'com.lzb.mpmt.test.model.User', 'com.lzb.mpmt.test.model.UserAddress', 'user', 'user_address', 'ONE', 'MANY', 1, 0, 'id', 'user_id');
INSERT INTO `multi_table_relation` VALUES ('user__user_staff', 'com.lzb.mpmt.test.model.User', 'com.lzb.mpmt.test.model.UserStaff', 'user', 'user_staff', 'ONE', 'MANY', 1, 0, 'id', 'admin_user_id');


INSERT INTO `user` VALUES (1, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'username1', '1', 1, NULL, NULL);
INSERT INTO `user` VALUES (2, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'username2', '1', 2, 1.00, NULL);


INSERT INTO `user_address` VALUES (111, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'province1', 1, 'street1', '1001');
INSERT INTO `user_address` VALUES (112, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'province2', 1, 'street2', '1001');
INSERT INTO `user_address` VALUES (113, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'province3', 1, 'street3', '1002');
INSERT INTO `user_address` VALUES (114, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'province4', 2, 'street4', '1003');


INSERT INTO `user_staff` VALUES (11, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'staff1', '0', 1);
INSERT INTO `user_staff` VALUES (12, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'staff2', '0', 1);
INSERT INTO `user_staff` VALUES (13, '2021-11-09 22:21:56', '2021-11-09 22:21:56', 'staff3', '0', 2);

