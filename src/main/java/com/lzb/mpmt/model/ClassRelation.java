package com.lzb.mpmt.model;

import com.lzb.mpmt.enums.ClassRelationOneOrManyEnum;
import com.lzb.mpmt.model.User;

import static com.lzb.mpmt.enums.ClassRelationOneOrManyEnum.*;

import com.lzb.mpmt.model.UserStaff;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.function.Function;

/**
 * 类和类的关系  可以自动生成join语句 统一规范sql 按表名字排序
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClassRelation {
    /***/
//    r1(User.class, UserStaff.class, ONE, MANY, true, false, User::getId);
    private Long id;

    //两张表名
    private Class<?> class1;
    private Class<?> class2;
    private String tableName1;
    private String tableName2;

    //一对一 一对多 多对一 多对多
    private ClassRelationOneOrManyEnum class1OneOrMany;
    private ClassRelationOneOrManyEnum class2OneOrMany;

    //关系中 是否是否,表1一定该有数据/表2一定该有数据
    private Boolean class1Require;
    private Boolean class2Require;

    //两个表关联的字段
    private String class1KeyProp;
    private String class2KeyProp;
}
