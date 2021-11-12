package com.lzb.mpmt.service.multiwrapper.wrapper;

import com.lzb.mpmt.service.multiwrapper.util.*;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperMain;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperMainSubWhere;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperSub;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.IMultiWrapperSubAndRelationTreeNode;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.MultiWrapperSubAndRelation;
import com.lzb.mpmt.service.multiwrapper.enums.JoinTypeEnum;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import com.lzb.mpmt.service.multiwrapper.entity.MultiTableRelation;
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
public class MultiWrapper<MAIN> {

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

    public MultiWrapper(MultiWrapperMain<MAIN> wrapperMain, Class<?>... subTableClasses) {
        this.wrapperMain = wrapperMain;
        Arrays.stream(subTableClasses).forEach(subTableClass -> this.leftJoin(MultiWrapperSub.lambda(subTableClass)));
    }

    /**
     * 主表信息
     * 例如 select * from user_staff
     *
     * @return MultiWrapper
     */
    public static <MAIN> MultiWrapper<MAIN> main(MultiWrapperMain<MAIN> wrapperMain) {
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
    public static <MAIN> MultiWrapper<MAIN> main(MultiWrapperMain<MAIN> wrapperMain, MultiWrapperMainSubWhere<?>... wrapperMainSubWhere) {
        MultiWrapper<MAIN> wrapper = new MultiWrapper<>();
        wrapper.setWrapperMain(wrapperMain);
        if (wrapperMainSubWhere != null) {
            wrapper.setWrapperMainSubWheres(Arrays.stream(wrapperMainSubWhere).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        return wrapper;
    }

    /***
     * join 副表信息
     *
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public <SUB> MultiWrapper<MAIN> leftJoin(MultiWrapperSub<SUB> subTableWrapper) {
        return leftJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     *
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public <SUB> MultiWrapper<MAIN> innerJoin(MultiWrapperSub<SUB> subTableWrapper) {
        return innerJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     *
     * @param relationCode      {@link MultiTableRelation#getCode()}
     * @param subTableWrapper 副表的select和 on内条件信息
     * @return MultiWrapper
     */
    public <SUB> MultiWrapper<MAIN> leftJoin(String relationCode, MultiWrapperSub<SUB> subTableWrapper) {
        JoinTypeEnum joinType = JoinTypeEnum.left_join;
        return this.getMainMultiWrapper(joinType, relationCode, subTableWrapper);
    }

    public <SUB> MultiWrapper<MAIN> innerJoin(String relationCode, MultiWrapperSub<SUB> subTableWrapper) {
        JoinTypeEnum joinType = JoinTypeEnum.inner_join;
        return this.getMainMultiWrapper(joinType, relationCode, subTableWrapper);
    }

    private <SUB> MultiWrapper<MAIN> getMainMultiWrapper(JoinTypeEnum joinType, String relationCode, MultiWrapperSub<SUB> subTableWrapper) {
        wrapperSubAndRelations.add(new MultiWrapperSubAndRelation<>(joinType, relationCode, subTableWrapper));
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
        // 1.1 解析 主表和副表的关系树
        relationTree = this.reloadRelations(wrapperMain, this.wrapperSubAndRelations);

        // 1.2 关系统一按树自顶向下排列
        this.wrapperSubAndRelations = new ArrayList<>();
        relationTree.consumerTopToBottom(relationNode -> {
            if (relationNode instanceof MultiWrapperSubAndRelation) {
                wrapperSubAndRelations.add((MultiWrapperSubAndRelation<?>) relationNode);
            }
        });

        // 2.1. 解析 select要查出的字段语句片段
        // select user_staff.* from user_staff
        List<String> selectPropsList = this.wrapperSubAndRelations.stream().map(o -> o.getWrapperSub().getSqlSelectProps(o.getRelationCode())).collect(Collectors.toList());
        selectPropsList.add(0, wrapperMain.getSqlSelectProps(wrapperMain.getTableName()));
        String sqlSelect = "\nselect\n" + selectPropsList.stream().filter(Objects::nonNull).collect(Collectors.joining(",\n"));

        // 3. 解析 from主表,limit主表语句片段
        //	SELECT u.*,p.* FROM user_info                          u LEFT JOIN principal_user p ON p.user_id = u.id where p.admin_flag = 1;
        //	SELECT u.*,p.* FROM (select * from user_info limit 10) u LEFT JOIN principal_user p ON p.user_id = u.id where p.admin_flag = 1;
        String sqlFromLimit = "\nFROM " + wrapperMain.getSqlFromLimit(mainTableName);


        String sqlLeftJoinOn = "\n" + this.wrapperSubAndRelations.stream().map(r -> r.getSqlJoin(mainTableName)).collect(Collectors.joining("\n"));

        // 4. 解析 where条件语句片段
        //    where user_staff.state = 0
        //      and user_staff_address.del_flag = 0
        List<MultiWrapperWhere<?, ?>> whereWrappers = new ArrayList<>(wrapperMainSubWheres);
        whereWrappers.add(0, wrapperMain);
        String wherePropsAppend = whereWrappers.stream().map(MultiWrapperWhere::getSqlWhereProps).filter(s -> !MultiUtil.isEmpty(s)).collect(Collectors.joining("\n  and "));
        String sqlWhere = MultiUtil.isEmpty(wherePropsAppend) ? MultiConstant.Strings.EMPTY : "\nwhere 1=1\n  and" + wherePropsAppend;

        return sqlSelect + sqlFromLimit + sqlLeftJoinOn + sqlWhere;
    }

    private TreeNode<IMultiWrapperSubAndRelationTreeNode> reloadRelations(MultiWrapperMain<MAIN> wrapperMain, List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations) {
        //relationCode可能缺省,去关系表中加载
        this.fillRelationCodeAndTableThisOther(wrapperMain.getTableName(), wrapperSubAndRelations);

        List<IMultiWrapperSubAndRelationTreeNode> relationsAndMain = new ArrayList<>(wrapperSubAndRelations);
        relationsAndMain.add(wrapperMain);

        //构建关系树  将用在
        // 1.在按顺序生成left join语句(否则会报错)
        // 2.按顺序映射查询结果到实体类上
        return TreeNode.buildTree(relationsAndMain, o -> o, o -> o, o -> o instanceof MultiWrapperMain);
    }

    private void fillRelationCodeAndTableThisOther(String mainTableName, List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations) {
        List<MultiWrapperSubAndRelation<?>> hasCodeRelations = wrapperSubAndRelations.stream().filter(relation -> null != relation.getRelationCode()).collect(Collectors.toList());
        List<MultiWrapperSubAndRelation<?>> noCodeRelations = wrapperSubAndRelations.stream().filter(relation -> null == relation.getRelationCode()).collect(Collectors.toList());

        //已经确定关系的表
        Set<String> relationTableNames = new HashSet<>();
        relationTableNames.add(mainTableName);
        hasCodeRelations.forEach(relationHasCode -> {
            String subTableName = relationHasCode.getWrapperSub().getTableName();
            relationTableNames.add(relationHasCode.getWrapperSub().getTableName());
            MultiTableRelation relation = MultiTableRelationFactory.INSTANCE.getRelationCodeMap().get(relationHasCode.getRelationCode());
            this.fillTableThisAndOther(relationHasCode, subTableName, relation);
        });

        noCodeRelations.forEach(noCodeRelation ->
                {
                    String subTableName = noCodeRelation.getWrapperSub().getTableName();
                    boolean hasRelation = false;
                    for (String relationTableName : relationTableNames) {
                        List<MultiTableRelation> relations = MultiTableRelationFactory.INSTANCE.getRelation2TableNameMap()
                                .getOrDefault(subTableName, Collections.emptyMap())
                                .getOrDefault(relationTableName, Collections.emptyList());
                        if (relations.size() > 1) {
                            //有多种关系,需要重新确定
                            throw new MultiException(relationTableName + "和" + subTableName + "存在多种关系,需要手动指定relationCode");
                        }
                        if (relations.size() < 1) {
                            //有多种关系,需要重新确定
                            throw new MultiException(relationTableName + "和" + subTableName + "没有存在表关系,无法关联");
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
    }

    private void fillTableThisAndOther(MultiWrapperSubAndRelation<?> noCodeRelation, String subTableName, MultiTableRelation multiTableRelation) {
        String tableName1 = multiTableRelation.getTableName1();
        String tableName2 = multiTableRelation.getTableName2();
        if (tableName1.equals(subTableName)) {
            noCodeRelation.setTableNameThis(tableName1);
            noCodeRelation.setTableNameOther(tableName2);
            noCodeRelation.setSubTableOneOrMany(multiTableRelation.getClass2OneOrMany());
        } else if (tableName2.equals(subTableName)) {
            noCodeRelation.setTableNameThis(tableName2);
            noCodeRelation.setTableNameOther(tableName1);
            noCodeRelation.setTableNameOther(tableName1);
            noCodeRelation.setSubTableOneOrMany(multiTableRelation.getClass1OneOrMany());
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
        return MultiTableRelationFactory.INSTANCE.getRelationCodeMap().get(relationCode);
    }

    public List<MAIN> list() {
        return Collections.emptyList();
    }
}
