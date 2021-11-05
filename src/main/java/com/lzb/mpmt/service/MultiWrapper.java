package com.lzb.mpmt.service;

import com.lzb.mpmt.service.multiwrapper.enums.JoinTypeEnum;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.joindata.MultiTableRelation;
import com.lzb.mpmt.service.multiwrapper.util.MultiConstant;
import com.lzb.mpmt.service.multiwrapper.util.MultiException;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
import com.lzb.mpmt.service.multiwrapper.util.TreeNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 多表联查器
 * 举例说明 主表:user_staff 副表:user_staff_address
 *
 * @param <MAIN>
 * @author Administrator
 */
@Data
@NoArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class MultiWrapper<MAIN extends MultiModel> {

    /**
     * 主表信息
     */
    private MultiWrapperMain<MAIN> wrapperMain;
    private List<MultiWrapperMainSubWhere<?>> wrapperMainSubWheres = Collections.emptyList();

    /***
     * 计算SQL时,初始化表关系树
     */
    private TreeNode<IMultiWrapperSubAndRelationTreeNode> relationTree;

    /**
     * 副表信息
     */
    private List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations = new ArrayList<>(8);

    public MultiWrapper(MultiWrapperMain<MAIN> wrapperMain, MultiWrapperSub<?>... subTableWrappers) {
        this.wrapperMain = wrapperMain;
        //默认leftJoin
        Arrays.stream(subTableWrappers).forEach(this::leftJoin);
    }

    public MultiWrapper(MultiWrapperMain<MAIN> wrapperMain, Class<? extends MultiModel>... subTableClasses) {
        this.wrapperMain = wrapperMain;
        Arrays.stream(subTableClasses).forEach(subTableClass -> this.leftJoin(MultiWrapperSub.lambda(subTableClass)));
    }

    /**
     * 主表信息
     * 例如 select * from user_staff
     *
     * @return MultiWrapper
     */
    public static <MAIN extends MultiModel> MultiWrapper<MAIN> main(MultiWrapperMain<MAIN> wrapperMain) {
        return main(wrapperMain, (MultiWrapperMainSubWhere<?>[]) null);
    }

    /**
     * 主表信息
     * 例如 select * from user_staff u
     * left join user_staff_address a on a.staff_id = u.id
     * where user_staff_address.del_flag = 0
     *
     * @return MultiWrapper
     */
    public static <MAIN extends MultiModel> MultiWrapper<MAIN> main(MultiWrapperMain<MAIN> wrapperMain, MultiWrapperMainSubWhere<?>... wrapperMainSubWhere) {
        MultiWrapper<MAIN> wrapper = new MultiWrapper<>();
        wrapper.setWrapperMain(wrapperMain);
        if (wrapperMainSubWhere != null) {
            wrapper.setWrapperMainSubWheres(Arrays.stream(wrapperMainSubWhere).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        return wrapper;
    }

    /***
     * join 副表信息
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public <SUB extends MultiModel> MultiWrapper<MAIN> leftJoin(MultiWrapperSub<SUB> subTableWrapper) {
        return leftJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public <SUB extends MultiModel> MultiWrapper<MAIN> innerJoin(MultiWrapperSub<SUB> subTableWrapper) {
        return innerJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     *
     * @param relationCode      {@link MultiTableRelation#getCode()}
     * @param subTableWrapper 副表的select和 on内条件信息
     * @return MultiWrapper
     */
    public <SUB extends MultiModel> MultiWrapper<MAIN> leftJoin(String relationCode, MultiWrapperSub<SUB> subTableWrapper) {
        JoinTypeEnum joinType = JoinTypeEnum.left_join;
        return this.getMainMultiWrapper(joinType, relationCode, subTableWrapper);
    }

    public <SUB extends MultiModel> MultiWrapper<MAIN> innerJoin(String relationCode, MultiWrapperSub<SUB> subTableWrapper) {
        JoinTypeEnum joinType = JoinTypeEnum.inner_join;
        return this.getMainMultiWrapper(joinType, relationCode, subTableWrapper);
    }

    private <SUB extends MultiModel> MultiWrapper<MAIN> getMainMultiWrapper(JoinTypeEnum joinType, String relationCode, MultiWrapperSub<SUB> subTableWrapper) {
        wrapperSubAndRelations.add(new MultiWrapperSubAndRelation<>(joinType, relationCode, subTableWrapper));
        return this;
    }


    /***
     * 参数可以统一格式map传递(容易缺失编译约束,不建议后端自查询频繁使用)
     * @param allTableParamMap allTableParamMap
     * @return MultiWrapper
     */
    public MultiWrapper<MAIN> allTableParamMap(Map<String, Object> allTableParamMap) {
        //可能还有分页的limit 按主表去limit
        return this;
    }

    /**
     * 输出最终sql
     */
    public String computeSql() {
        String mainTableName = wrapperMain.getTableName();
        if (mainTableName == null) {
            throw new MultiException("请先通过MultiWrapperMain.lambda(UserInfo.class)或者.eq(UserInfo::getId)确定表名,在执行查询");
        }
        // 3. left join user_staff_address on user_staff.id = user_staff_address.staff_id
        // 加载默认的关系配置 将关系排序
        relationTree = this.reloadRelations(wrapperMain.getTableName(), this.wrapperSubAndRelations);
        //关系列表需要,按树从顶向下排序
        this.wrapperSubAndRelations = new ArrayList<>();
        relationTree.consumerTopToBottom(relationNode -> {
            if (relationNode instanceof MultiWrapperSubAndRelation) {
                wrapperSubAndRelations.add((MultiWrapperSubAndRelation<?>) relationNode);
            }
        });

        // 1. select user_staff.* from user_staff
        /* 是否有主表副表存在两个关系(副表ID,对应主表两个属性) */
//        Boolean hasSameRelation = hasSameRelation(wrapperSubAndRelations);
        List<String> selectPropsList = this.wrapperSubAndRelations.stream().map(o -> o.getWrapperSub().getSqlSelectProps(o.getRelationCode())).collect(Collectors.toList());
        selectPropsList.add(0, wrapperMain.getSqlSelectProps(wrapperMain.getTableName()));
        String sqlSelect = "\nselect\n" + selectPropsList.stream().filter(Objects::nonNull).collect(Collectors.joining(",\n"));

        // 2. 添加limit
        //	SELECT u.*,p.* FROM user_info                          u LEFT JOIN principal_user p ON p.user_id = u.id where p.admin_flag = 1;
        //	SELECT u.*,p.* FROM (select * from user_info limit 10) u LEFT JOIN principal_user p ON p.user_id = u.id where p.admin_flag = 1;
        String sqlFromLimit = "\nFROM " + wrapperMain.getSqlFromLimit(mainTableName);


        String sqlLeftJoinOn = "\n" + this.wrapperSubAndRelations.stream().map(r -> r.getSqlJoin(mainTableName)).collect(Collectors.joining("\n"));

        // 4. where user_staff.state = 0
        //      and user_staff_address.del_flag = 0
        List<MultiWrapperWhere<?, ?>> whereWrappers = new ArrayList<>(wrapperMainSubWheres);
        whereWrappers.add(0, wrapperMain);
        String wherePropsAppend = whereWrappers.stream().map(MultiWrapperWhere::getSqlWhereProps).filter(s -> !MultiUtil.isEmpty(s)).collect(Collectors.joining("\n  and "));
        String sqlWhere = MultiUtil.isEmpty(wherePropsAppend) ? MultiConstant.Strings.EMPTY : "\nwhere 1=1\n  and" + wherePropsAppend;

        return sqlSelect + sqlFromLimit + sqlLeftJoinOn + sqlWhere;
    }

    private TreeNode<IMultiWrapperSubAndRelationTreeNode> reloadRelations(String mainTableName, List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations) {
        //relationCode可能缺省,默认去关系表中加载
        this.setRelationCodeAndTableName12(mainTableName, wrapperSubAndRelations);

        List<IMultiWrapperSubAndRelationTreeNode> relationsAndMain = new ArrayList<>(wrapperSubAndRelations);
        relationsAndMain.add(new MultiWrapperSubAndRelationTreeNodeMain(mainTableName));

        //构建关系树  将用在
        // 1.在按顺序生成left join语句(否则会报错)
        // 2.按顺序映射查询结果到实体类上
        return TreeNode.buildTree(relationsAndMain, o -> o, o -> o, o -> o instanceof MultiWrapperSubAndRelationTreeNodeMain);
    }

    private void setRelationCodeAndTableName12(String mainTableName, List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations) {
        List<MultiWrapperSubAndRelation<?>> hasCodeRelations = wrapperSubAndRelations.stream().filter(relation -> null != relation.getRelationCode()).collect(Collectors.toList());
        List<MultiWrapperSubAndRelation<?>> noCodeRelations = wrapperSubAndRelations.stream().filter(relation -> null == relation.getRelationCode()).collect(Collectors.toList());

        //已经确定关系的表
        Set<String> relationTableNames = new HashSet<>();
        relationTableNames.add(mainTableName);
        hasCodeRelations.forEach(relationHasCode -> {
            String subTableName = relationHasCode.getWrapperSub().getTableName();
            relationTableNames.add(relationHasCode.getWrapperSub().getTableName());
            MultiTableRelation relation = MultiWrapperSubAndRelation.MULTI_TABLE_RELATION_FACTORY.getRelationCodeMap().get(relationHasCode.getRelationCode());
            this.fillTableThisAndOther(relationHasCode, subTableName, relation);
        });

        noCodeRelations.forEach(noCodeRelation ->
                {
                    String subTableName = noCodeRelation.getWrapperSub().getTableName();
                    boolean hasRelation = false;
                    for (String relationTableName : relationTableNames) {
                        List<MultiTableRelation> relations = MultiWrapperSubAndRelation.MULTI_TABLE_RELATION_FACTORY.getRelation2TableNameMap()
                                .getOrDefault(subTableName, Collections.emptyMap())
                                .getOrDefault(relationTableName, Collections.emptyList());
                        if (relations.size() > 1) {
                            //有多种关系,需要重新确定
                            throw new MultiException(relationTableName + "和" + subTableName + "存在多种关系,需要手动指定relationCode");
                        }
                        if (relations.size() < 1) {
                            break;
                            //有多种关系,需要重新确定
//                            throw new MultiException(relationTableName + "和" + subTableName + "没有存在表关系,无法关联");
                        }
                        MultiTableRelation relation = relations.get(0);

                        noCodeRelation.setRelationCode(relation.getCode());
                        this.fillTableThisAndOther(noCodeRelation, subTableName, relation);

                        hasRelation = true;
                        break;
                    }
                    if (hasRelation) {
                        relationTableNames.add(subTableName);
                    } else {
                        throw new MultiException(subTableName + "和[" + String.join(",", relationTableNames) + "]没有存在表关系,无法关联");
                    }
                }
        );
        wrapperSubAndRelations.forEach(r->{
            r.getWrapperSub().setIdFieldName(r.getRelationCode() + "." + MultiUtil.camelToUnderline(r.getWrapperSub().getIdField().getName()));
        });
    }

    private void fillTableThisAndOther(MultiWrapperSubAndRelation<?> noCodeRelation, String subTableName, MultiTableRelation multiTableRelation) {
        String tableName1 = multiTableRelation.getTableName1();
        String tableName2 = multiTableRelation.getTableName2();
        if (tableName1.equals(subTableName)) {
            noCodeRelation.setTableNameThis(tableName1);
            noCodeRelation.setTableNameOther(tableName2);
        } else if (tableName2.equals(subTableName)) {
            noCodeRelation.setTableNameThis(tableName2);
            noCodeRelation.setTableNameOther(tableName1);
        } else {
            throw new MultiException("表关系" + multiTableRelation.getCode() + "(" + tableName1 + "," + tableName2 + ")其中之一必须和当前查询的表" + subTableName);
        }
    }

    /**
     *
     */
    private void addToListNew(List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelationsNew, Set<String> parentTableNames, List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations) {
        ArrayList<MultiWrapperSubAndRelation<?>> multiWrapperSubAndRelationsTemp = new ArrayList<>(wrapperSubAndRelations);

        List<MultiWrapperSubAndRelation<?>> subRelations = wrapperSubAndRelations.stream().filter(relation ->
                {
                    MultiTableRelation relation1Now = getRelationByCode(relation.getRelationCode());
                    return parentTableNames.stream().anyMatch(parentTable -> relation1Now.getTableNames().contains(parentTable));
                }
        ).collect(Collectors.toList());
        wrapperSubAndRelationsNew.addAll(subRelations);
        multiWrapperSubAndRelationsTemp.removeAll(subRelations);

        //添加父节点表名
        subRelations.forEach(subRelation -> parentTableNames.addAll(getRelationByCode(subRelation.getRelationCode()).getTableNames()));

        if (multiWrapperSubAndRelationsTemp.size() > 0) {
            //递归添加
            addToListNew(wrapperSubAndRelationsNew, parentTableNames, multiWrapperSubAndRelationsTemp);
        }
    }

    private MultiTableRelation getRelationByCode(String relationCode) {
        return MultiWrapperSubAndRelation.MULTI_TABLE_RELATION_FACTORY.getRelationCodeMap().get(relationCode);
    }

//    private static Boolean hasSameRelation(List<MultiWrapperSubAndRelation<?>> relations) {
//        Set<String> tableSets = relations.stream().map(r -> r.getWrapperSub().getTableName()).collect(Collectors.toSet());
//        return tableSets.size() != relations.size();
//    }

    public List<MAIN> list() {
        return Collections.emptyList();
    }
}
