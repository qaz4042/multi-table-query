package com.lzb.mpmt.enums.common;

import com.lzb.mpmt.model.User;
import com.lzb.mpmt.model.UserStaff;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 类和类的关系  可以自动生成left join 统一规范sql
 */
@AllArgsConstructor
@Getter
public enum TableEnum {
    /***/
    User(User.class, "用户"),
    UserStaff(UserStaff.class, "用户的员工"),
    UserStaffShopProductRate(UserStaff.class, "用户的员工"),
    ;

    Class<?> clazz;
    String label;
}

