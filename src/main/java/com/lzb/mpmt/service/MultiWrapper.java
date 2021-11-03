package com.lzb.mpmt.service;

import com.lzb.mpmt.service.multiwrapper.enums.JoinTypeEnum;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.joindata.MultiTableRelation;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
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
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public <SUB> MultiWrapper<MAIN> leftJoin(MultiWrapperSub<SUB> subTableWrapper) {
        return leftJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
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
            throw new RuntimeException("请先通过MultiWrapperMain.lambda(UserInfo.class)或者.eq(UserInfo::getId)确定表名,在执行查询");
        }
        // 1. select user_staff.* from user_staff
        /* 是否有主表副表存在两个关系(副表ID,对应主表两个属性) */
        Boolean hasSameRelation = hasSameRelation(wrapperSubAndRelations);
        List<String> selectPropsList = wrapperSubAndRelations.stream().map(o -> o.getWrapperSub().getSqlSelectProps(hasSameRelation, o.getRelationCode())).collect(Collectors.toList());
        selectPropsList.add(0, wrapperMain.getSqlSelectProps(hasSameRelation, ""));
        String sqlSelect = "\nselect\n" + selectPropsList.stream().filter(Objects::nonNull).collect(Collectors.joining(",\n"));

        // 2. 添加limit
        //	SELECT u.*,p.* FROM user_info                          u LEFT JOIN principal_user p ON p.user_id = u.id where p.admin_flag = 1;
        //	SELECT u.*,p.* FROM (select * from user_info limit 10) u LEFT JOIN principal_user p ON p.user_id = u.id where p.admin_flag = 1;
        String sqlFromLimit = "\nFROM " + wrapperMain.getSqlFromLimit(mainTableName);

        // 3. left join user_staff_address on user_staff.id = user_staff_address.staff_id
        // 加载默认的关系配置 todo  wrapperSubAndRelations 没有指定关系的当前leftjoin的表必须跟前面的某张表有直接关系
        wrapperSubAndRelations = reloadRelations(wrapperMain.getTableName(), wrapperSubAndRelations);
        String sqlLeftJoinOn = "\n" + wrapperSubAndRelations.stream().map(r -> r.getSqlJoin(mainTableName)).collect(Collectors.joining("\n"));

        // 4. where user_staff.state = 0
        //      and user_staff_address.del_flag = 0
        List<MultiWrapperWhere<?, ?>> whereWrappers = new ArrayList<>(wrapperMainSubWheres);
        whereWrappers.add(0, wrapperMain);
        String wherePropsAppend = whereWrappers.stream().map(MultiWrapperWhere::getSqlWhereProps).filter(s -> !MultiUtil.isEmpty(s)).collect(Collectors.joining("\n  and "));
        String sqlWhere = MultiUtil.isEmpty(wherePropsAppend) ? MultiUtil.EMPTY : "\nwhere 1=1\n  and" + wherePropsAppend;

        return sqlSelect + sqlFromLimit + sqlLeftJoinOn + sqlWhere;
    }

    private List<MultiWrapperSubAndRelation<?>> reloadRelations(String mainTableName, List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations) {
        List<MultiWrapperSubAndRelation<?>> hasCodeRelations = wrapperSubAndRelations.stream().filter(relation -> null != relation.getRelationCode()).collect(Collectors.toList());
        List<MultiWrapperSubAndRelation<?>> noCodeRelations = wrapperSubAndRelations.stream().filter(relation -> null == relation.getRelationCode()).collect(Collectors.toList());

        //已经确定关系的表
        Set<String> relationTableNames = new HashSet<>();
        relationTableNames.add(mainTableName);
        hasCodeRelations.forEach(relationHasCode -> relationTableNames.add(relationHasCode.getWrapperSub().getTableName()));

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
                            throw new RuntimeException(relationTableName + "和" + subTableName + "存在多种关系,需要手动指定relationCode");
                        }
                        if (relations.size() < 1) {
                            break;
                            //有多种关系,需要重新确定
//                            throw new RuntimeException(relationTableName + "和" + subTableName + "没有存在表关系,无法关联");
                        }
                        noCodeRelation.setRelationCode(relations.get(0).getCode());
                        hasRelation = true;
                        break;
                    }
                    if (hasRelation) {
                        relationTableNames.add(subTableName);
                    } else {
                        throw new RuntimeException(subTableName + "和[" + String.join(",", relationTableNames) + "]没有存在表关系,无法关联");
                    }
                }
        );

        //按主表 -> 一层副表 -> 二层副本 ...逐级在wrapperSubAndRelationsNew中排列下去
        List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelationsNew = new ArrayList<>(8);
        Set<String> parentTableNames = new HashSet<>();
        parentTableNames.add(mainTableName);
        addToListNew(wrapperSubAndRelationsNew, parentTableNames, wrapperSubAndRelations);

        return wrapperSubAndRelationsNew;
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

    private static Boolean hasSameRelation(List<MultiWrapperSubAndRelation<?>> relations) {
        Set<String> tableSets = relations.stream().map(r -> r.getWrapperSub().getTableName()).collect(Collectors.toSet());
        return tableSets.size() != relations.size();
    }

    public List<MAIN> list() {
        return Collections.emptyList();
    }
}
