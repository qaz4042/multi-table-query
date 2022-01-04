package com.bebetter.mtq.service.multiwrapper.wrapper.inner;

import com.bebetter.mtq.service.multiwrapper.constant.MultiConstant;
import com.bebetter.mtq.service.multiwrapper.entity.MultiClassRelation;
import com.bebetter.mtq.service.multiwrapper.sqlsegment.MultiWrapperAggregate;
import com.bebetter.mtq.service.multiwrapper.sqlsegment.MultiWrapperSelect;
import com.bebetter.mtq.service.multiwrapper.sqlsegment.wheredata.WhereDataUnit;
import com.bebetter.mtq.service.multiwrapper.util.*;
import com.bebetter.mtq.service.multiwrapper.util.json.jackson.JsonUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 多表联查器
 * 举例说明 主表:user_staff 副表:user_staff_address
 *
 * @param <MAIN, DTO>
 * @author Administrator
 */
@NoArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class MultiWrapperInner<MAIN, DTO> {

    /**
     * 主表信息
     */
    public MultiWrapperMainInner<MAIN, DTO> wrapperMain;

    /**
     * 副表信息
     */
    public List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations = new ArrayList<>(8);


    /**
     * 计算SQL时,初始化表关系树
     */
    public MultiTreeNode<IMultiWrapperSubAndRelationTreeNode> relationTree;


    /**
     * 参数Map 例如
     * {
     * "userAndUserStaff.balance":"#gt#100", //其中userAndUserStaff是relationCode
     * "userStaff.sex":"#eq#1"
     * }
     */
    public Map<String, List<WhereDataUnit>> extendParamMap = Collections.emptyMap();


    public MultiWrapperInner(MultiWrapperMainInner<MAIN, DTO> wrapperMain) {
        this.wrapperMain = wrapperMain;
    }

    public MultiWrapperInner(MultiWrapperMainInner<MAIN, DTO> wrapperMain, MultiWrapperSubInner<?>... subTableWrappers) {
        this.wrapperMain = wrapperMain;
        //默认leftJoin
        Arrays.stream(subTableWrappers).forEach(this::leftJoin);
    }

    public MultiWrapperInner(MultiWrapperMainInner<MAIN, DTO> wrapperMain, Class<?>... subTableClasses) {
        this.wrapperMain = wrapperMain;
        Arrays.stream(subTableClasses).forEach(subTableClass -> this.leftJoin(MultiWrapperSubInner.lambda(subTableClass)));
    }

    /**
     * 主表信息
     * 例如 select * from user_staff
     *
     * @return MultiWrapper
     */
    public static <MAIN, DTO> MultiWrapperInner<MAIN, DTO> main(MultiWrapperMainInner<MAIN, DTO> wrapperMain) {
        MultiWrapperInner<MAIN, DTO> wrapper = new MultiWrapperInner<>();
        wrapper.wrapperMain = wrapperMain;
        return wrapper;
    }

    /***
     * join 副表信息
     *
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public MultiWrapperInner<MAIN, DTO> leftJoin(MultiWrapperSubInner<?> subTableWrapper) {
        return leftJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     *
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public MultiWrapperInner<MAIN, DTO> innerJoin(MultiWrapperSubInner<?> subTableWrapper) {
        return innerJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     *
     * @param relationCode      {@link MultiClassRelation#getCode()}
     * @param subTableWrapper 副表的select和 on内条件信息
     * @return MultiWrapper
     */
    public MultiWrapperInner<MAIN, DTO> leftJoin(String relationCode, MultiWrapperSubInner<?> subTableWrapper) {
        MultiConstant.JoinTypeEnum joinType = MultiConstant.JoinTypeEnum.left_join;
        return this.getMainMultiWrapper(joinType, relationCode, subTableWrapper);
    }

    public MultiWrapperInner<MAIN, DTO> innerJoin(String relationCode, MultiWrapperSubInner<?> subTableWrapper) {
        MultiConstant.JoinTypeEnum joinType = MultiConstant.JoinTypeEnum.inner_join;
        return this.getMainMultiWrapper(joinType, relationCode, subTableWrapper);
    }

    public MultiWrapperInner<MAIN, DTO> getMainMultiWrapper(MultiConstant.JoinTypeEnum joinType, String relationCode, MultiWrapperSubInner<?> subTableWrapper) {
        wrapperSubAndRelations.add(new MultiWrapperSubAndRelation<>(joinType, relationCode, subTableWrapper));
        return this;
    }

    /**
     * @param extendParams 参数Map例如:
     *                     {
     *                     "userAndUserStaff_balance":"100#%#", //其中userAndUserStaff是relationCode,  like '张三%' 才能走索引
     *                     "userStaff_sex":"1"
     *                     }
     */
    public MultiWrapperInner<MAIN, DTO> extendParams(Map<String, ?> extendParams) {
        Map<String, List<WhereDataUnit>> extendParamMap = new LinkedHashMap<>();
        for (String key : extendParams.keySet()) {
            String[] keys = key.split("_");
            if (keys.length != 2) {
                log.warn("参数" + key + "格式异常,没有参与过滤|应类似\"userAndUserStaff_balance\"(relationCode_propName)");
                continue;
            }
            String relationCode = keys[0];
            String propName = keys[1];
            Object value = extendParams.get(key);
            MultiConstant.WhereOptEnum opt = MultiConstant.WhereOptEnum.eq;
            if (value instanceof String) {
                String valueStr = (String) value;
                if (valueStr.indexOf("#") == 0 && valueStr.indexOf("#", 1) == 3) {
                    String optPrefix = valueStr.substring(0, 4);
                    opt = MultiConstant.WhereOptEnum.PARAM_MAP_OPT_PREFIX_MAP.get(optPrefix);
                    MultiUtil.assertNoNull(opt, "无效的查询条件操作类型{}|{} {}", optPrefix, key, value);
                    value = ((String) value).substring(4);
                }
            }
            extendParamMap.computeIfAbsent(relationCode, c -> new ArrayList<>(8)).add(new WhereDataUnit(propName, opt, value));
        }
        this.extendParamMap = extendParamMap;
        return this;
    }

    /**
     * 输出最终sql
     */
    public String computeAggregateSql() {
        String mainClassName = wrapperMain.getClassName();
        MultiUtil.assertNoNull(mainClassName, "请先通过MultiWrapperMain.lambda(UserInfo.class)或者.eq(UserInfo::getId)确定表名,在执行查询");

        this.loadRelations();
        String mainTableIdFieldName = MultiUtil.camelToUnderline(MultiRelationCaches.getTableIdField(wrapperMain.getClazz()).getName());
        //全部字段聚合 select sum(t1.amount),sum(t2.qty) ...
        // sum/avg全部数字型字段 count(1) countDistinct全部字段
        List<MultiConstant.MultiAggregateTypeEnum> aggregateAllTypes = this.wrapperMain.getAggregateAllTypes();
        List<String> aggregateFields = new ArrayList<>(32);
        if (aggregateAllTypes.size() > 0) {
            aggregateFields.addAll(
                    aggregateAllTypes.stream().flatMap(aggregateAllType ->
                            MultiConstant.MultiAggregateTypeEnum.COUNT.equals(aggregateAllType) ?
                                    Stream.of("count(DISTINCT " + mainClassName + "." + mainTableIdFieldName + ") as \"COUNT. . \"") :
                                    Stream.of(
                                            this.computeAggregateFieldAssOne(this.wrapperMain, mainClassName, aggregateAllType),
                                            this.wrapperSubAndRelations.stream().flatMap(
                                                    multiWrapperSubAndRelation -> this.computeAggregateFieldAssOne(multiWrapperSubAndRelation.getWrapperSub(), multiWrapperSubAndRelation.getRelationCode(), aggregateAllType)
                                            )
                                    ).flatMap(l -> l)
                    ).collect(Collectors.toList())
            );
            System.out.println("aggregateFieldAss" + JsonUtil.toString(aggregateFields));
            //过滤掉非数字的属性
        }
        this.getAggregateInfosRecursion(this.relationTree, aggregateFields);

        if (MultiUtil.isEmpty(aggregateFields)) {
            throw new MultiException("没有可以(要)聚合的列,无法查询");
        }

        //指定字段聚合
        String sqlSelect = "select " + String.join(",\n", aggregateFields);
        String sqlFromLimit = "\nfrom " + MultiUtil.camelToUnderline(mainClassName) + " " + mainClassName;
        String sqlLeftJoinOn = "\n" + String.join("\n", getSqlJoinRecursion(this.relationTree));
        String sqlWhere = this.getSqlWhere();

        return sqlSelect + sqlFromLimit + sqlLeftJoinOn + sqlWhere;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void getAggregateInfosRecursion(MultiTreeNode<IMultiWrapperSubAndRelationTreeNode> relationTree, List<String> aggregateFields) {
        IMultiWrapperSubAndRelationTreeNode curr = relationTree.getCurr();
        if (curr instanceof MultiWrapperMainInner) {
            aggregateFields.addAll(((MultiWrapperMainInner) curr).getSqlAggregate(relationTree.getCurr().getRelationCode()));
        } else if (curr instanceof MultiWrapperSubAndRelation) {
            aggregateFields.addAll(((MultiWrapperSubAndRelation) curr).getWrapperSub().getSqlAggregate(relationTree.getCurr().getRelationCode()));
        }
        relationTree.getChildren().forEach(child -> getAggregateInfosRecursion(child, aggregateFields));
    }


    /**
     * 输出最终sql
     */
    public String computeSql() {
        String mainClassName = wrapperMain.getClassName();
        MultiUtil.assertNoNull(mainClassName, "请先通过MultiWrapperMain.lambda(UserInfo.class)或者.eq(UserInfo::getId)确定表名,在执行查询");

        // 1. 解析 关系
        this.loadRelations();

        // 2.1. 解析 select要查出的字段语句片段
        // select user_staff.* from user_staff
        List<String> selectPropsList = this.wrapperSubAndRelations.stream().map(o -> o.getWrapperSub().getSqlSelectProps(o.getRelationCode())).collect(Collectors.toList());
        selectPropsList.add(0, wrapperMain.getSqlSelectProps(wrapperMain.getClassName()));
        String sqlSelect = "\nselect\n" + selectPropsList.stream().filter(Objects::nonNull).collect(Collectors.joining(",\n"));

        // 3. 解析 from主表,limit主表语句片段
        //	SELECT u.*,p.* FROM user_info                          u LEFT JOIN principal_user p ON p.user_id = u.id where p.admin_flag = 1;
        //	SELECT u.*,p.* FROM (select * from user_info limit 10) u LEFT JOIN principal_user p ON p.user_id = u.id where p.admin_flag = 1;
        String sqlFromLimit = "\nFROM " + wrapperMain.getSqlFromLimit(mainClassName);

        String sqlLeftJoinOn = "\n" + String.join("\n", getSqlJoinRecursion(this.relationTree));

        String sqlWhere = this.getSqlWhere();

        return sqlSelect + sqlFromLimit + sqlLeftJoinOn + sqlWhere;
    }


    public Stream<String> computeAggregateFieldAssOne(MultiWrapperSelect<?, ?> multiWrapperSelect, String relationCode, MultiConstant.MultiAggregateTypeEnum aggregateAllType) {
        Map<String, MultiTuple2<Field, Method>> classInfos = MultiRelationCaches.getClassInfos(multiWrapperSelect.getClazz());
        return multiWrapperSelect.getSelectFieldNames().stream()
                .filter(fieldName -> {
                            //过滤部分字段
                            MultiTuple2<Field, Method> fieldInfo = classInfos.get(fieldName);
                            MultiUtil.assertNoNull(fieldInfo, "{0}不存在{1}属性", multiWrapperSelect.getClazz(), fieldName);
                            Class<?> relationFieldType = fieldInfo.getT1().getType();
                            return aggregateAllType.getFieldTypeFilter().apply(relationFieldType);
                        }
                ).map(fieldName -> MultiWrapperAggregate.appendOneField(aggregateAllType, relationCode, fieldName, null));
    }

    public List<String> getSqlJoinRecursion(MultiTreeNode<IMultiWrapperSubAndRelationTreeNode> relationTree) {
        String relationCode = relationTree.getCurr().getRelationCode();
        List<String> leftJoins = relationTree.getChildren().stream().map(r -> {
            if (r.getCurr() instanceof MultiWrapperSubAndRelation) {
                //noinspection rawtypes
                MultiWrapperSubAndRelation<?> curr = (MultiWrapperSubAndRelation) r.getCurr();
                String subRelationCode = curr.getRelationCode();
                MultiClassRelation relation = MultiClassRelationFactory.INSTANCE.getRelationCodeMap().get(subRelationCode);
                boolean thisIs1 = curr.getClassNameThis().equals(relation.getClassName1());
                String leftJsonOn = " left join " + MultiUtil.camelToUnderline(curr.getClassNameThis()) + " " + subRelationCode + " on "
                        + subRelationCode + "." + MultiUtil.camelToUnderline(thisIs1 ? relation.getClass1KeyProp() : relation.getClass2KeyProp())
                        + "="
                        + relationCode + "." + MultiUtil.camelToUnderline(thisIs1 ? relation.getClass2KeyProp() : relation.getClass1KeyProp());
                String sqlWhereSub = curr.getWrapperSub().getSqlWhereProps(curr.getRelationCode(), this.extendParamMap.get(curr.getRelationCode()));
                return leftJsonOn + (MultiUtil.isEmpty(sqlWhereSub) ? MultiConstant.Strings.EMPTY : " and " + sqlWhereSub);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<String> leftJoinsSub = relationTree.getChildren().stream().flatMap(child -> getSqlJoinRecursion(child).stream()).collect(Collectors.toList());
        leftJoins.addAll(leftJoinsSub);
        return leftJoins;
    }

    public String getSqlWhere() {
        // 4. 解析 where条件语句片段
        //    where user_staff.state = 0
        //      and user_staff_address.del_flag = 0
        List<String> sqlWheres = new ArrayList<>();

        String sqlWhereMain = this.wrapperMain.getSqlWhereProps(wrapperMain.getRelationCode(), this.extendParamMap.get(wrapperMain.getRelationCode()));
        List<String> sqlWhereSubMain = this.getSqlMainWhMerePropsMainRecursion(this.relationTree, this.extendParamMap);
        sqlWheres.add(sqlWhereMain);
        sqlWheres.addAll(sqlWhereSubMain);

        String sqlWhereAppend = String.join("\n and ", sqlWheres);
        return MultiUtil.isEmpty(sqlWhereAppend) ? MultiConstant.Strings.EMPTY : "\nwhere 1=1\n  and " + sqlWhereAppend;
    }

    public List<String> getSqlMainWhMerePropsMainRecursion(MultiTreeNode<IMultiWrapperSubAndRelationTreeNode> treeNode, Map<String, List<WhereDataUnit>> extendParamMap) {
        List<String> sqlChildren = treeNode.getChildren().stream().map(treeNodeChild -> {
            IMultiWrapperSubAndRelationTreeNode curr = treeNodeChild.getCurr();
            if (curr instanceof MultiWrapperSubAndRelation) {
                MultiWrapperSubMainWhereInner<?> mainWhere = ((MultiWrapperSubAndRelation<?>) curr).getWrapperSub().getMainWhere();
                if (mainWhere != null) {
                    return mainWhere.getSqlWhereProps(curr.getRelationCode(), extendParamMap.get(curr.getRelationCode()));
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<String> sqlGrandChildren = treeNode.getChildren().stream().flatMap(treeNodeChild -> getSqlMainWhMerePropsMainRecursion(treeNodeChild, extendParamMap).stream()).filter(Objects::nonNull).collect(Collectors.toList());
        sqlChildren.addAll(sqlGrandChildren);
        return sqlChildren;
    }

    public void loadRelations() {
        if (relationTree == null) {
            // 1.1 解析 主表和副表的关系树
            relationTree = this.reloadRelations(wrapperMain, this.wrapperSubAndRelations);

            // 1.2 关系统一按树自顶向下排列
            this.wrapperSubAndRelations = new ArrayList<>();
            relationTree.consumerTopToBottom(relationNode -> {
                if (relationNode instanceof MultiWrapperSubAndRelation) {
                    wrapperSubAndRelations.add((MultiWrapperSubAndRelation<?>) relationNode);
                }
            });
        }
    }

    public MultiTreeNode<IMultiWrapperSubAndRelationTreeNode> reloadRelations(MultiWrapperMainInner<MAIN, DTO> wrapperMain, List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations) {
        //relationCode可能缺省,去关系表中加载
        this.fillRelationCodeAndTableThisOther(wrapperMain, wrapperSubAndRelations);

        List<IMultiWrapperSubAndRelationTreeNode> relationsAndMain = new ArrayList<>(wrapperSubAndRelations);
        relationsAndMain.add(wrapperMain);

        //构建关系树  将用在
        // 1.在按顺序生成left join语句(否则会报错)
        // 2.按顺序映射查询结果到实体类上
        return MultiTreeNode.buildTree(relationsAndMain, o -> o, o -> o, o -> o instanceof MultiWrapperMainInner);
    }

    public void fillRelationCodeAndTableThisOther(MultiWrapperMainInner<MAIN, DTO> wrapperMain, List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations) {
        String mainClassName = wrapperMain.getClassName();

        List<MultiWrapperSubAndRelation<?>> hasCodeRelations = wrapperSubAndRelations.stream().filter(relation -> null != relation.getRelationCode()).collect(Collectors.toList());
        List<MultiWrapperSubAndRelation<?>> noCodeRelations = wrapperSubAndRelations.stream().filter(relation -> null == relation.getRelationCode()).collect(Collectors.toList());

        //已经确定关系的表
        Set<String> relationClassNames = new HashSet<>();
        relationClassNames.add(mainClassName);
        hasCodeRelations.forEach(relationHasCode -> {
            String subClassName = relationHasCode.getWrapperSub().getClassName();
            relationClassNames.add(relationHasCode.getWrapperSub().getClassName());
            MultiClassRelation relation = MultiClassRelationFactory.INSTANCE.getRelationCodeMap().get(relationHasCode.getRelationCode());
            this.fillTableThisAndOther(relationHasCode, subClassName, relation);
        });

        noCodeRelations.forEach(noCodeRelation ->
                {
                    String subClassName = noCodeRelation.getWrapperSub().getClassName();
                    boolean hasRelation = false;
                    for (String relationClassName1 : relationClassNames) {
                        List<MultiClassRelation> relations = MultiClassRelationFactory.INSTANCE.getRelation2ClassNameMap()
                                .getOrDefault(subClassName, Collections.emptyMap())
                                .getOrDefault(relationClassName1, Collections.emptyList());
                        if (relations.size() > 1) {
                            //有多种关系,需要重新确定
                            throw new MultiException(relationClassName1 + "和" + subClassName + "存在多种关系,需要手动指定relationCode");
                        }
                        if (relations.size() < 1) {
                            continue;
                        }
                        MultiClassRelation relation = relations.get(0);

                        noCodeRelation.setRelationCode(relation.getCode());
                        this.fillTableThisAndOther(noCodeRelation, subClassName, relation);

                        hasRelation = true;
                        break;
                    }
                    if (hasRelation) {
                        relationClassNames.add(subClassName);
                    } else {
                        throw new MultiException(subClassName + "和[" + String.join(",", relationClassNames) + "]没有存在表关系,无法关联");
                    }
                }
        );
    }

    public void fillTableThisAndOther(MultiWrapperSubAndRelation<?> noCodeRelation, String subClassName, MultiClassRelation multiTableRelation) {
        String className1 = multiTableRelation.getClassName1();
        String className2 = multiTableRelation.getClassName2();
        if (className1.equals(subClassName)) {
            noCodeRelation.setClassNameThis(className1);
            noCodeRelation.setClassNameThisOneOrMany(multiTableRelation.getClass1OneOrMany());
            noCodeRelation.setClassNameOtherRequire(multiTableRelation.getClass1Require());
            noCodeRelation.setClassNameOther(className2);
        } else if (className2.equals(subClassName)) {
            noCodeRelation.setClassNameThis(className2);
            noCodeRelation.setClassNameThisOneOrMany(multiTableRelation.getClass2OneOrMany());
            noCodeRelation.setClassNameOtherRequire(multiTableRelation.getClass2Require());
            noCodeRelation.setClassNameOther(className1);
        } else {
            throw new MultiException("表关系" + multiTableRelation.getCode() + "(" + className1 + "," + className2 + ")其中之一必须和当前查询的表" + subClassName);
        }
    }

    /**
     *
     */
    public void addToListNew(List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelationsNew, Set<String> parentClassNames, List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations) {
        ArrayList<MultiWrapperSubAndRelation<?>> multiWrapperSubAndRelationsTemp = new ArrayList<>(wrapperSubAndRelations);

        List<MultiWrapperSubAndRelation<?>> subRelations = wrapperSubAndRelations.stream().filter(relation ->
                {
                    MultiClassRelation relation1Now = getRelationByCode(relation.getRelationCode());
                    return parentClassNames.stream().anyMatch(parentClass -> relation1Now.getClassNames().contains(parentClass));
                }
        ).collect(Collectors.toList());
        wrapperSubAndRelationsNew.addAll(subRelations);
        multiWrapperSubAndRelationsTemp.removeAll(subRelations);

        //添加父节点表名
        subRelations.forEach(subRelation -> parentClassNames.addAll(getRelationByCode(subRelation.getRelationCode()).getClassNames()));

        if (multiWrapperSubAndRelationsTemp.size() > 0) {
            //递归添加
            addToListNew(wrapperSubAndRelationsNew, parentClassNames, multiWrapperSubAndRelationsTemp);
        }
    }

    public MultiClassRelation getRelationByCode(String relationCode) {
        return MultiClassRelationFactory.INSTANCE.getRelationCodeMap().get(relationCode);
    }

    public MultiWrapperMainInner<MAIN, DTO> getWrapperMain() {
        return wrapperMain;
    }

    public MultiTreeNode<IMultiWrapperSubAndRelationTreeNode> getRelationTree() {
        return relationTree;
    }
}
