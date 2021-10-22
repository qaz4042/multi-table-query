package com.lzb.mpmt.enums;

import com.lzb.mpmt.model.User;

import static com.lzb.mpmt.enums.ClassRelationOneOrManyEnum.*;

import com.lzb.mpmt.model.UserStaff;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类和类的关系  可以自动生成left join 统一规范sql
 */
@AllArgsConstructor
@Getter
public enum ClassRelationEnum implements IClassRelationEnum{
    /***/
    r1(User.class, UserStaff.class, ONE, MANY, true, false);

    Class<?> class1;
    Class<?> class2;
    ClassRelationOneOrManyEnum class1One;
    ClassRelationOneOrManyEnum class2One;
    boolean class1Require;
    boolean class2Require;

}
