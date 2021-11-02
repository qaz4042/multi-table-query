package com.lzb.mpmt.service;

import com.lzb.mpmt.enums.ClassRelationOneOrManyEnum;
import com.lzb.mpmt.model.ClassRelation;
import com.lzb.mpmt.enums.JoinTypeEnum;
import com.lzb.mpmt.model.User;
import com.lzb.mpmt.model.UserStaff;
import com.lzb.mpmt.service.util.MutilUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSubAndRelation<SUB> {
    //聚合方式
    private JoinTypeEnum joinType;
    /**
     * 关系 ClassRelation.id {@link com.lzb.mpmt.model.ClassRelation#getId()}
     */
    private Long relationId;

    //内容
    private MultiWrapperSub<SUB> wrapperSub;

    //todo
    static List<ClassRelation> relationsAll = new ArrayList<>();

    static {
        relationsAll.add(
                ClassRelation.builder()
                        .id(1L)
                        .class1(User.class)
                        .tableName1("user")
                        .class1KeyProp("id")
                        .class1OneOrMany(ClassRelationOneOrManyEnum.ONE)
                        .class1Require(true)

                        .class2(UserStaff.class)
                        .tableName2("user_staff")
                        .class2KeyProp("admin_user_id")
                        .class2OneOrMany(ClassRelationOneOrManyEnum.MANY)
                        .class2Require(false)
                        .build()
        );
    }

    public String getSqlJoin(String mainTableName) {
        String subTableName = wrapperSub.getTableName();

        ClassRelation relation = relationsAll.stream().filter(r -> r.getId().equals(relationId)).findFirst().orElse(null);
        if (relation == null) {
            relation = relationsAll.stream().filter(r -> (mainTableName.equals(r.getTableName1()) && subTableName.equals(r.getTableName2()))
                    || (mainTableName.equals(r.getTableName2()) && subTableName.equals(r.getTableName1()))).findFirst().orElse(null);
        }
        if (relation == null) {
            throw new RuntimeException("表关联不存在,mainTableName=" + mainTableName + ",subTableName=" + subTableName);
        }
        boolean mainTableIs1 = mainTableName.equals(relation.getTableName1());
        String mainTableKeyProp = mainTableIs1 ? relation.getClass1KeyProp() : relation.getClass2KeyProp();
        String subTableKeyProp = mainTableIs1 ? relation.getClass2KeyProp() : relation.getClass1KeyProp();

        String sqlWhereProps = getWrapperSub().getSqlWhereProps();
        sqlWhereProps = MutilUtil.isEmpty(sqlWhereProps) ? MutilUtil.EMPTY : " and " + sqlWhereProps;
        // todo tableName 用小写
        return joinType.getSql() + subTableName + " on " + subTableName + "." + subTableKeyProp + " = " + mainTableName + "." + mainTableKeyProp + sqlWhereProps;
    }
}
