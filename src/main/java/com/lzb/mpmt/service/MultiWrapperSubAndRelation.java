package com.lzb.mpmt.service;

import com.lzb.mpmt.service.multiwrapper.enums.JoinTypeEnum;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.joindata.MultiTableRelation;
import com.lzb.mpmt.service.multiwrapper.util.MultiTableRelationFactory;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 副表,和主副表对应关系信息
 *
 * @param <SUB> 副表泛型
 * @author Administrator
 */
@Data
@NoArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class MultiWrapperSubAndRelation<SUB> {

    public static MultiTableRelationFactory MULTI_TABLE_RELATION_FACTORY;

    public MultiWrapperSubAndRelation(JoinTypeEnum joinType, String relationCode, MultiWrapperSub<SUB> wrapperSub) {
        this.joinType = joinType;
        this.relationCode = relationCode;
        this.wrapperSub = wrapperSub;
    }

    /**
     * 聚合方式
     */
    private JoinTypeEnum joinType;

    /**
     * 关系 ClassRelation.code {@link MultiTableRelation#getCode()}
     */
    private String relationCode;

    /**
     * 副表信息
     */
    private MultiWrapperSub<SUB> wrapperSub;

//    public static List<TableRelation> getRelationsAll() {
//        return relationsAll;
//    }

//    static {
//        relationsAll.add(
//                TableRelation.builder()
//                        .id(1L)
////                        .class1(User.class)     todo 数据初始化
//                        .tableName1("user")
//                        .class1KeyProp("id")
//                        .class1OneOrMany(ClassRelationOneOrManyEnum.ONE)
//                        .class1Require(true)
//
////                        .class2(UserStaff.class) todo 数据初始化
//                        .tableName2("user_staff")
//                        .class2KeyProp("admin_user_id")
//                        .class2OneOrMany(ClassRelationOneOrManyEnum.MANY)
//                        .class2Require(false)
//                        .build()
//        );
//    }

    public String getSqlJoin(String mainTableName) {
        if (MULTI_TABLE_RELATION_FACTORY == null) {
            throw new RuntimeException("请先初始化表关系 MultiWrapperSubAndRelation.MultiTableRelationFactory");
        }
        MultiTableRelation relation = MULTI_TABLE_RELATION_FACTORY.getRelationCodeMap().get(relationCode);
        String subTableName = wrapperSub.getTableName();

//
//        MultiTableRelation relation = null;
//        if (relationCode != null) {
//            relation = MULTI_TABLE_RELATION_FACTORY.getRelationCodeMap().get(relationCode);
//            if (relation == null) {
//                // todo mainTableName
//                //默认取第一个关系
//                List<MultiTableRelation> relations = MULTI_TABLE_RELATION_FACTORY.getRelation2TableNameMap()
//                        .getOrDefault(mainTableName, Collections.emptyMap())
//                        .getOrDefault(subTableName, Collections.emptyList());
//                if (relations.size() > 0) {
//                    if (relations.size() > 1) {
//                        log.warn("");
//                    }
//                    relation = relations.get(0);
//                }
//            } else {
//
//            }
//
//            if (relation == null) {
//                throw new RuntimeException("表关联不存在,mainTableName=" + mainTableName + ",subTableName=" + subTableName);
//            }
//
//            //关系,需要当前副表,跟已知的主表副表都有关系
//            Set<String> relationTableNames = new HashSet<>(Arrays.asList(relation.getTableName1(), relation.getTableName2()));
//            boolean containsCurrSubTableName = relationTableNames.contains(wrapperSub.getTableName());
//            boolean containsOneOffTables = relationTableNames.contains(wrapperSub.getTableName());
//        }

//        boolean mainTableIs1 = mainTableName.equals(relation.getTableName1());
//        String mainTableKeyProp = mainTableIs1 ? relation.getClass1KeyProp() : relation.getClass2KeyProp();
//        String subTableKeyProp = mainTableIs1 ? relation.getClass2KeyProp() : relation.getClass1KeyProp();
//        relation.getTableName1()
        String sqlWhereProps = getWrapperSub().getSqlWhereProps();
        sqlWhereProps = MultiUtil.isEmpty(sqlWhereProps) ? MultiUtil.EMPTY : " and " + sqlWhereProps;
        return joinType.getSql() + subTableName + " on " + relation.getTableName1() + "." + relation.getClass1KeyProp() + " = " + relation.getTableName2() + "." + relation.getClass2KeyProp() + sqlWhereProps;
    }
}
