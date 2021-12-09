package com.lzb.mpmt.service.multiwrapper.executor;

import com.lzb.mpmt.service.multiwrapper.config.MultiProperties;
import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;
import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant.ClassRelationOneOrManyEnum;
import com.lzb.mpmt.service.multiwrapper.dto.IMultiPage;
import com.lzb.mpmt.service.multiwrapper.dto.MultiAggregateResult;
import com.lzb.mpmt.service.multiwrapper.dto.MultiAggregateResultMap;
import com.lzb.mpmt.service.multiwrapper.dto.MultiHashMap;
import com.lzb.mpmt.service.multiwrapper.entity.IMultiEnum;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.IMultiSqlExecutor;
import com.lzb.mpmt.service.multiwrapper.util.*;
import com.lzb.mpmt.service.multiwrapper.util.json.jackson.JsonUtil;
import com.lzb.mpmt.service.multiwrapper.wrapper.inner.MultiWrapperInner;
import com.lzb.mpmt.service.multiwrapper.wrapper.inner.IMultiWrapperSubAndRelationTreeNode;
import com.lzb.mpmt.service.multiwrapper.wrapper.inner.MultiWrapperMainInner;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Slf4j
@Service
public class MultiExecutorInner {

    private static IMultiSqlExecutor executor;
    private static MultiProperties multiProperties;

    @Autowired
    public void setExecutor(IMultiSqlExecutor executor) {
        MultiExecutorInner.executor = executor;
    }

    @Autowired
    public void setMultiProperties(MultiProperties multiProperties) {
        MultiExecutorInner.multiProperties = multiProperties;
    }

    @SneakyThrows
    public static <MAIN> List<MAIN> list(MultiWrapperInner<MAIN> wrapper) {
        String sql = wrapper.computeSql();
        //执行sql
        Map<String, Object> relationIdObjectMap = new HashMap<>(2048);
        List<MAIN> mains = executor.select(sql, (resultSet) -> {
            MultiTuple2<MAIN, Boolean> mainAndIsNew = buildReturnRecursion(MultiConstant.Strings.EMPTY, null, wrapper.getRelationTree(), resultSet, relationIdObjectMap, false);
            MAIN main = mainAndIsNew.getT1();
            return mainAndIsNew.getT2() ? main : null;
        }).stream().filter(Objects::nonNull).collect(Collectors.toList());

        log.info("Multi 查询结果:{}条", mains.size());

        if (multiProperties.getCheckRelationRequire()) {
            //检查表关系中,一方有数据,另一方必须有数据,是否有异常数据(测试环境可以开启)
            checkRequireRecursion(wrapper.getWrapperMain().getClazz(), mains, wrapper.getRelationTree().getChildren());
        }
        return mains;
    }

    @SneakyThrows
    public static <MAIN> MAIN getOne(MultiWrapperInner<MAIN> wrapper) {
        List<MAIN> list = list(wrapper);
        if (list.size() > 1) {
            log.warn("getOne查询出两条数据:" + wrapper.computeSql());
        }
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 执行分页查询
     *
     * @param page    分页信息
     * @param wrapper 表信息,聚合信息,过滤条件...
     * @return 聚合查询结果 例如 {"sum":{"userAndUserStaff.balance":"100.00"}}
     */
    @SneakyThrows
    public static <MAIN> IMultiPage<MAIN> page(IMultiPage<MAIN> page, MultiWrapperInner<MAIN> wrapper) {
        MultiWrapperMainInner<MAIN> wrapperMain = wrapper.getWrapperMain();
        boolean containsAggregate = wrapperMain.getAggregateAllTypes().size() + wrapperMain.getAggregateInfos().size() > 0;

        Long count;
        MultiAggregateResult aggregateResult;
        List<MAIN> list = Collections.emptyList();


        //查询聚合
        wrapperMain.count();
        aggregateResult = aggregate(wrapper);
        count = aggregateResult.getCount();

        if (count > 0) {
            //清理聚合信息(才能执行普通列表查询)
            wrapperMain.aggregateBackupAndClear();
            //填充分页信息
            wrapperMain.limit((page.getCurrPage() - 1) * page.getPageSize(), page.getPageSize());
            //查询列表
            list = list(wrapper);

            //恢复聚合信息为了不更改源对象
            wrapperMain.aggregateRestore();
        }


        //独立 count (如果wrapper里面有可以直接取)
        page.setTotal(count);
        page.setAggregateResult(containsAggregate ? aggregateResult : null);
        page.setRecords(list);
        return page;
    }

    /**
     * 执行聚合查询
     *
     * @param wrapper 表信息,聚合信息,过滤条件...
     * @return 聚合查询结果 例如 {"sum":{"userAndUserStaff.balance":"100.00"}}
     */
    @SneakyThrows
    public static <MAIN> MultiAggregateResult aggregate(MultiWrapperInner<MAIN> wrapper) {
        String aggregateSql = wrapper.computeAggregateSql();

        Map<String, ?> objectMap = executor.selectFirstRow(aggregateSql);

        MultiAggregateResult aggregateResult = new MultiAggregateResult();
        Map<MultiAggregateResultMap, ? extends Map.Entry<String, ?>> map = MultiUtil.listToMap(objectMap.entrySet(), e -> new MultiAggregateResultMap(e.getKey()));
        map.entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().getAggregateType())).forEach((aggregateType, list) -> {
            MultiHashMap<String, ?> keyValueMap = list.stream().collect(Collectors.toMap(e -> e.getKey().getRelationCode() + "." + e.getKey().getPropName(), e -> e.getValue().getValue(), (v1, v2) -> v1, MultiHashMap::new));
            switch (aggregateType) {
                case SUM:
                    aggregateResult.setSum(keyValueMap);
                    break;
                case AVG:
                    aggregateResult.setAvg(keyValueMap);
                    break;
                case COUNT:
                    aggregateResult.setCount(Long.parseLong(list.get(0).getValue().getValue().toString()));
                    break;
                case COUNT_DISTINCT:
                    aggregateResult.setCountDistinct(keyValueMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> null == e.getValue() ? 0 : Long.parseLong(e.getValue().toString()), (v1, v2) -> v1, MultiHashMap::new)));
                    break;
                case MAX:
                    aggregateResult.setMax(keyValueMap);
                    break;
                case MIN:
                    aggregateResult.setMin(keyValueMap);
                    break;
                case GROUP_CONCAT:
                    //noinspection unchecked
                    aggregateResult.setGroupConcat((MultiHashMap<String, String>) keyValueMap);
                    break;
                default:
                    throw new MultiException(aggregateType + "该聚合方法待实现");
            }
        });
        log.info("Multi 查询结果:{}", aggregateResult);
        return aggregateResult;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private static <MAIN_OR_SUB> void checkRequireRecursion(Class<?> currClass, List<MAIN_OR_SUB> currDatas, List<MultiTreeNode<IMultiWrapperSubAndRelationTreeNode>> subRelationNodes) {
        if (MultiUtil.isEmpty(currDatas)) {
            return;
        }
        for (MultiTreeNode<IMultiWrapperSubAndRelationTreeNode> relationTreeNode : subRelationNodes) {
            IMultiWrapperSubAndRelationTreeNode curr = relationTreeNode.getCurr();
            String relationCode = curr.getRelationCode();
            Boolean subTableRequire = curr.getClassNameOtherRequire();

            for (MAIN_OR_SUB currData : currDatas) {
                Method getMethod = MultiRelationCaches.getTableWithTable_getSetMethod(currClass, relationCode).getT1();
                Object subValues = getMethod.invoke(currData);
                if (subTableRequire) {
                    //检查当前
                    if (subValues == null) {
                        throwRequireException(currClass.getSimpleName(), relationCode, currData);
                    }
                    if (subValues instanceof List) {
                        if (((List<?>) subValues).size() == 0) {
                            throwRequireException(currClass.getSimpleName(), relationCode, currData);
                        }
                    }
                }
                if (subValues == null) {
                    //为空子表没数据检查,跳过
                    continue;
                }
                List<MAIN_OR_SUB> subs;
                if (subValues instanceof List && ((List<?>) subValues).size() > 0) {
                    subs = (List<MAIN_OR_SUB>) subValues;
                } else {
                    subs = (List<MAIN_OR_SUB>) Collections.singletonList(subValues);
                }
                //递归检查
                checkRequireRecursion(relationTreeNode.getCurr().getTableClassThis(), subs, relationTreeNode.getChildren());
            }
        }
    }

    private static <MAIN_OR_SUB> void throwRequireException(String classNameThis, String relationCode, MAIN_OR_SUB currData) {
        throw new MultiException(classNameThis + "表在" + relationCode + "关系中,需要另一张表一定有数据,但没有|" + classNameThis + "表数据:" + JsonUtil.toString(currData));
    }

    /**
     * 递归构造子表对象
     *
     * @param <MAIN_OR_SUB>       父表对象的泛型
     * @param parentCodeAppendId  父实体的唯一code+ID(多层级)
     * @param parentEntity        父表对象
     * @param relationTreeNode    父表和子表当前的关系(.chlidren()是子表和他的子表的关系)
     * @param resultSet           sql执行后的每一行结果集
     * @param relationIdObjectMap 已经构造好的对象,如果有id,需要按id合并子表信息到list中
     * @param parentIsNew         父节点是不是新增节点
     * @return 新的父表对象信息(旧的副表对象信息)
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows
    private static <MAIN_OR_SUB> MultiTuple2<MAIN_OR_SUB, Boolean> buildReturnRecursion(
            String parentCodeAppendId,
            MAIN_OR_SUB parentEntity,
            MultiTreeNode<IMultiWrapperSubAndRelationTreeNode> relationTreeNode,
            ResultSet resultSet,
            Map<String, Object> relationIdObjectMap,
            boolean parentIsNew
    ) {
        IMultiWrapperSubAndRelationTreeNode currNode = relationTreeNode.getCurr();
        Class<?> currTableClass = currNode.getTableClassThis();
        String currRelationCode = currNode.getRelationCode();
        Field currTableIdField = MultiRelationCaches.getTableIdField(currTableClass);
        String idFieldName = currRelationCode + "." + currTableIdField.getName();
        Object id = getValue(idFieldName, currTableIdField.getType(), resultSet);
        String relationIdObjectMapKey = parentCodeAppendId + "_" + idFieldName + "_" + id;
        MAIN_OR_SUB currEntity = (MAIN_OR_SUB) relationIdObjectMap.get(relationIdObjectMapKey);
        //要新增元素
        boolean isNew = parentIsNew || currEntity == null;
        if (isNew) {
            //重复则不在生成
            currEntity = (MAIN_OR_SUB) currTableClass.newInstance();

            Map<String, MultiTuple2<Field, Method>> classInfos = MultiRelationCaches.getClassInfos(currTableClass);
            MultiUtil.assertNoNull(classInfos, "找不到{0}对应的类", currTableClass);

            List<String> selectFieldNames = currNode.getMultiWrapperSelectInfo().getSelectFields();
            for (String selectFieldName : selectFieldNames) {

                MultiTuple2<Field, Method> filedInfos = classInfos.get(selectFieldName);
                MultiUtil.assertNoNull(filedInfos, "找不到{0}对应的属性{1}", currTableClass, selectFieldName);

                Class<?> fieldReturnType = filedInfos.getT1().getType();
                Method fieldSetMethod = filedInfos.getT2();
                Object value = getValue(currRelationCode + "." + selectFieldName, fieldReturnType, resultSet);
                fieldSetMethod.invoke(currEntity, value);
            }

            //顶层节点为null,不用出setSubEntity(subEntity);
            if (parentEntity != null) {
                setCurrEntityInToParent(currNode, parentEntity, currTableClass, currRelationCode, currEntity);
            }
        }

        relationIdObjectMap.put(relationIdObjectMapKey, currEntity);
        //副表信息,要递推填充下去
        MAIN_OR_SUB finalCurrEntity = currEntity;
        relationTreeNode.getChildren().forEach(subNode -> buildReturnRecursion(relationIdObjectMapKey, finalCurrEntity, subNode, resultSet, relationIdObjectMap, isNew));
        return new MultiTuple2<>(currEntity, isNew);
    }

    @SneakyThrows
    private static <MAIN_OR_SUB> void setCurrEntityInToParent(IMultiWrapperSubAndRelationTreeNode currNode, MAIN_OR_SUB parentEntity, Class<?> currTableClass, String currRelationCode, MAIN_OR_SUB currEntity) {
        MultiTuple2<Method, Method> getSetMethods = MultiRelationCaches.getTableWithTable_getSetMethod(parentEntity.getClass(), currRelationCode);
        Method getMethod = getSetMethods.getT1();
        Method setMethod = getSetMethods.getT2();
        Object subEntityExists = getMethod.invoke(parentEntity);
        Class<?> returnType = getMethod.getReturnType();

        //列表
        String classNameParent = currNode.getClassNameOther();
        String classNameThis = currNode.getClassNameThis();
        if (List.class.isAssignableFrom(returnType)) {
            if (!ClassRelationOneOrManyEnum.MANY.equals(currNode.getClassNameThisOneOrMany())) {
                log.warn(classNameParent + "与" + classNameThis + "不是1对多(或者多对多)关系,但" + currTableClass + "中" + currRelationCode + "为数组,定义不一致");
            }
            if (subEntityExists == null) {
                subEntityExists = new ArrayList<>(8);
                setMethod.invoke(parentEntity, subEntityExists);
            }
            //noinspection unchecked
            ((Collection<MAIN_OR_SUB>) subEntityExists).add(currEntity);
        } else if (returnType.isArray()) {
            throw new MultiException("暂时不支持array类型参数:" + getMethod);
        } else {
            if (multiProperties.getCheckRelationOneOrMany() && !ClassRelationOneOrManyEnum.ONE.equals(currNode.getClassNameThisOneOrMany())) {
                throw new MultiException(classNameParent + "与" + classNameThis + "不是1对1(或者多对多)关系,但" + currTableClass + "中" + currRelationCode + "为对象,定义不一致");
            }
            //一对一元素
            if (subEntityExists == null) {
                subEntityExists = currEntity;
                setMethod.invoke(parentEntity, subEntityExists);
            } else {
                log.warn("relationCode=" + currRelationCode + "|一对一,但查询出多个id不同的元素");
            }
        }
    }

    @SneakyThrows
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object getValue(String fieldName, Class<?> type, ResultSet resultSet) {
        if (Long.class.equals(type)) {
            return resultSet.getLong(fieldName);
        } else if (String.class.equals(type)) {
            return resultSet.getString(fieldName);
        } else if (Integer.class.equals(type)) {
            return resultSet.getInt(fieldName);
        } else if (BigDecimal.class.equals(type)) {
            return resultSet.getBigDecimal(fieldName);
        } else if (Date.class.equals(type)) {
            return resultSet.getDate(fieldName);
        } else if (Boolean.class.equals(type)) {
            return resultSet.getBoolean(fieldName);
        } else if (LocalDateTime.class.equals(type)) {
            return MultiUtil.date2LocalDateTime(resultSet.getDate(fieldName));
        } else if (LocalDate.class.equals(type)) {
            return MultiUtil.date2LocalDateTime(resultSet.getDate(fieldName)).toLocalDate();
        } else if (LocalTime.class.equals(type)) {
            return MultiUtil.date2LocalDateTime(resultSet.getDate(fieldName)).toLocalTime();
        } else if (Enum.class.isAssignableFrom(type)) {
            if (IMultiEnum.class.isAssignableFrom(type)) {
                Integer value = resultSet.getInt(fieldName);
                return MultiUtil.getEnumByValue((Class<IMultiEnum>) type, value);
            } else {
                //默认用枚举的name存取
                String value = resultSet.getString(fieldName);
                //noinspection unchecked
                return MultiUtil.getEnumByName((Class<Enum>) type, value);
            }
        } else if (Float.class.equals(type)) {
            return resultSet.getFloat(fieldName);
        } else if (Double.class.equals(type)) {
            return resultSet.getDouble(fieldName);
        } else if (Blob.class.equals(type)) {
            return resultSet.getBlob(fieldName);
        }
        throw new MultiException("未知的数据类型|" + fieldName + "|" + type);
    }
}
