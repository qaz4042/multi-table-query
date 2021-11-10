package com.lzb.mpmt.service.multiwrapper.entity;

import com.lzb.mpmt.service.multiwrapper.enums.ClassRelationOneOrManyEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 类和类的关系  可以自动生成join语句 统一规范sql 按表名字排序
 *
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MultiTableRelation {
    /***/
    private String code;
//    r1(User.class, UserStaff.class, ONE, MANY, true, false, User::getId);

    /***
     * 两张表名
     */
    private Class<?> class1;
    private Class<?> class2;
    private String tableName1;
    private String tableName2;

    public Set<String> getTableNames() {
        return new HashSet<>(Arrays.asList(tableName1, tableName2));
    }

    /***
     * 一对一 一对多 多对一 多对多
     */
    private ClassRelationOneOrManyEnum class1OneOrMany;
    private ClassRelationOneOrManyEnum class2OneOrMany;

    /***
     * 关系中 是否是否,表1一定该有数据/表2一定该有数据
     */
    private Boolean class1Require;
    private Boolean class2Require;

    /***
     * 两个表关联的字段
     */
    private String class1KeyProp;
    private String class2KeyProp;
}
