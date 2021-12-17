package com.bebetter.mtq.service.multiwrapper.entity;

import com.bebetter.mtq.service.multiwrapper.constant.MultiConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 类和类的关系
 * 可以在数据库中储存(推荐),也可以从枚举中读取
 * 可以,统一按表名字排序(class1和class2,不同的Relation和Relation)
 *
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MultiClassRelation {
    /**
     * 表关系唯一编号,例如 userAndUserStaff
     */
    private String code;

//    /**
//     * 两张表名 (仅做展示用)
//     */
//    private Class<?> class1;
//    private Class<?> class2;
    /**
     * 两张表对应实体名称
     */
    private String className1;
    private String className2;

    public Set<String> getClassNames() {
        return new HashSet<>(Arrays.asList(className1, className2));
    }

    /***
     * 一对一 一对多 多对一 多对多
     */
    private MultiConstant.ClassRelationOneOrManyEnum class1OneOrMany;
    private MultiConstant.ClassRelationOneOrManyEnum class2OneOrMany;

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
