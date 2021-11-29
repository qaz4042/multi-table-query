package com.lzb.mpmt.service.multiwrapper.executor;

import com.lzb.mpmt.service.multiwrapper.config.MultiProperties;
import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;
import com.lzb.mpmt.service.multiwrapper.dto.IMultiPage;
import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant.ClassRelationOneOrManyEnum;
import com.lzb.mpmt.service.multiwrapper.dto.MultiAggregateResult;
import com.lzb.mpmt.service.multiwrapper.entity.IMultiEnum;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.IMultiSqlExecutor;
import com.lzb.mpmt.service.multiwrapper.util.*;
import com.lzb.mpmt.service.multiwrapper.util.json.jackson.JSONUtil;
import com.lzb.mpmt.service.multiwrapper.wrapper.MultiWrapper;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.IMultiWrapperSubAndRelationTreeNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class MultiExecutor {

    private static IMultiSqlExecutor executor;
    private static MultiProperties multiProperties;

    @Autowired
    public void setExecutor(IMultiSqlExecutor executor) {
        MultiExecutor.executor = executor;
    }

    @Autowired
    public void setMultiProperties(MultiProperties multiProperties) {
        MultiExecutor.multiProperties = multiProperties;
    }


    @SneakyThrows
    public static <MAIN> List<MAIN> list(MultiWrapper<MAIN> wrapper) {
        String sql = wrapper.computeSql();
        //执行sql
        Map<String, Object> relationIdObjectMap = new HashMap<>(2048);
        List<MAIN> mains = executor.executeSql(sql, (resultSet) -> {
            MultiTuple2<MAIN, Boolean> mainAndIsNew = buildReturnRecursion("", null, wrapper.getRelationTree(), resultSet, relationIdObjectMap, false);
            MAIN main = mainAndIsNew.getT1();
            return mainAndIsNew.getT2() ? main : null;
        }).stream().filter(Objects::nonNull).collect(Collectors.toList());

//        System.out.println(JSONUtil.toString(mains));

        log.info("Multi 查询结果{}条, sql:{}", mains.size(), sql);

        if (multiProperties.getCheckRelationRequire()) {
            //检查表关系中,一方有数据,另一方必须有数据,是否有异常数据(测试环境可以开启) MultiTableRelation relation = MultiTableRelationFactory.INSTANCE.getRelationCodeMap().get(currNode.getRelationCode());
            checkRequireRecursion(wrapper.getWrapperMain().getTableName(), mains, wrapper.getRelationTree().getChildren());
        }
        return mains;
    }

    @SneakyThrows
    public static <MAIN> MAIN getOne(MultiWrapper<MAIN> wrapper) {
        List<MAIN> list = list(wrapper);
        return list.isEmpty() ? null : list.get(0);
    }

    @SuppressWarnings("ConstantConditions")
    @SneakyThrows
    public static <MAIN> IMultiPage<MAIN> page(IMultiPage<MAIN> page, MultiWrapper<MAIN> wrapper) {
        wrapper.getWrapperMain().count().limit((page.getCurrPage() - 1) * page.getPageSize(), page.getPageSize());
        MultiAggregateResult aggregateResult = aggregate(wrapper);
        List<MAIN> list = list(wrapper);
        //独立 count (如果wrapper里面有可以直接取)
        page.setRecords(list);
        page.setTotal(aggregateResult.getCount());
        page.setAggregateResult(aggregateResult);
        return page;
    }

    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
    public static class MultiAggregateResultMap {
        private MultiConstant.MultiAggregateTypeEnum aggregateType;
        private String relationCode;
        private String fieldName;

        public MultiAggregateResultMap(String append) {
            String[] split = append.split("\\.");
            aggregateType = MultiConstant.MultiAggregateTypeEnum.valueOf(split[0]);
            relationCode = split[1];
            fieldName = split[2];
        }
    }

    @SneakyThrows
    public static <MAIN> MultiAggregateResult aggregate(MultiWrapper<MAIN> wrapper) {
        String aggregateSql = wrapper.computeAggregateSql();
        log.info("aggregateSql, sql:\n{}", aggregateSql);
        Map<String, ?> objectMap = executor.executeSql(aggregateSql);

        MultiAggregateResult aggregateResult = new MultiAggregateResult();
        Map<MultiAggregateResultMap, ? extends Map.Entry<String, ?>> map = MultiUtil.listToMap(objectMap.entrySet(), e -> new MultiAggregateResultMap(e.getKey()));
        map.entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().getAggregateType())).forEach((aggregateType, list) -> {
            Map<String, Object> keyValueMap = list.stream().collect(Collectors.toMap(e -> e.getKey().getRelationCode() + "." + MultiUtil.underlineToCamel(e.getKey().getFieldName()), Map.Entry::getValue));
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
                    aggregateResult.setCountDistinct(keyValueMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> null == e.getValue() ? 0 : Long.parseLong(e.getValue().toString()))));
                    break;
                case MAX:
                    aggregateResult.setMax(keyValueMap);
                    break;
                case MIN:
                    aggregateResult.setMin(keyValueMap);
                    break;
                case GROUP_CONCAT:
                    break;
            }
        });
        return aggregateResult;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private static <MAIN_OR_SUB> void checkRequireRecursion(String currTableName, List<MAIN_OR_SUB> currDatas, List<MultiTreeNode<IMultiWrapperSubAndRelationTreeNode>> subRelationNodes) {
        if (MultiUtil.isEmpty(currDatas)) {
            return;
        }
        for (MultiTreeNode<IMultiWrapperSubAndRelationTreeNode> relationTreeNode : subRelationNodes) {
            IMultiWrapperSubAndRelationTreeNode curr = relationTreeNode.getCurr();
            String relationCode = curr.getRelationCode();
            String tableNameThis = curr.getTableNameThis();
            Class<?> tableClassThis = curr.getTableClassThis();
            Boolean subTableRequire = curr.getTableNameOtherRequire();

            for (MAIN_OR_SUB currData : currDatas) {
                Method getMethod = MultiRelationCaches.getRelation_TableWithTable_getSetMethod(relationCode, tableClassThis).getT1();
                Object subValues = getMethod.invoke(currData);
                if (subTableRequire) {
                    //检查当前
                    if (subValues == null) {
                        throwRequireException(currTableName, relationCode, currData);
                    }
                    if (subValues instanceof List) {
                        if (((List<?>) subValues).size() == 0) {
                            throwRequireException(currTableName, relationCode, currData);
                        }
                    }
                }
                if (subValues == null) {
                    //为空子表没数据检查,跳过
                    continue;
                }
                if (subValues instanceof List && ((List<?>) subValues).size() > 0) {
                    //递归检查
                    checkRequireRecursion(tableNameThis, (List<MAIN_OR_SUB>) subValues, relationTreeNode.getChildren());
                } else {
                    //递归检查
                    checkRequireRecursion(tableNameThis, Collections.singletonList(subValues), relationTreeNode.getChildren());
                }
            }
        }
    }

    private static <MAIN_OR_SUB> void throwRequireException(String tableNameThis, String relationCode, MAIN_OR_SUB currData) {
        throw new MultiException(tableNameThis + "表在" + relationCode + "关系中,需要另一张表一定有数据,但没有|" + tableNameThis + "表数据:" + JSONUtil.toString(currData));
    }

//    @SneakyThrows
//    public static <MAIN> List<MAIN> page(MultiPageOrder page, MultiWrapper<MAIN> wrapper) {
//    }

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
//        String currTableNameThis = currNode.getTableNameThis();
        Field currTableIdField = MultiRelationCaches.getTableIdField(currTableClass);
        String idFieldName = currRelationCode + "." + MultiUtil.camelToUnderline(currTableIdField.getName());
        Object id = getValue(idFieldName, currTableIdField.getType(), resultSet);
        String relationIdObjectMapKey = parentCodeAppendId + "_" + idFieldName + "_" + id;
        MAIN_OR_SUB currEntity = (MAIN_OR_SUB) relationIdObjectMap.get(relationIdObjectMapKey);
        //要新增元素
        boolean isNew = parentIsNew || currEntity == null;
        if (isNew) {
            //重复则不在生成
            //noinspection deprecation
            currEntity = (MAIN_OR_SUB) currTableClass.newInstance();
            List<String> selectFieldNames = currNode.getMultiWrapperSelectInfo().getSelectFields();
            for (String selectFieldName : selectFieldNames) {
                Class<?> fieldReturnType = MultiRelationCaches.getRelation_fieldType(currRelationCode, selectFieldName, currTableClass);
                Object value = getValue(currRelationCode + "." + selectFieldName, fieldReturnType, resultSet);
                MultiRelationCaches.getRelation_setMethod(currRelationCode, selectFieldName, currTableClass).invoke(currEntity, value);
            }

            //顶层节点为null,不用出setSubEntitys(subEntitys);
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
        MultiTuple2<Method, Method> getSetMethods = MultiRelationCaches.getRelation_TableWithTable_getSetMethod(currRelationCode, parentEntity.getClass());
        Method getMethod = getSetMethods.getT1();
        Method setMethod = getSetMethods.getT2();
        Object subEntityExists = getMethod.invoke(parentEntity);
        Class<?> returnType = getMethod.getReturnType();

        //列表
        String tableNameParent = currNode.getTableNameOther();
        String tableNameThis = currNode.getTableNameThis();
        if (List.class.isAssignableFrom(returnType)) {
            if (!ClassRelationOneOrManyEnum.MANY.equals(currNode.getTableNameThisOneOrMany())) {
//                        throw new MultiException(currNode.getTableNameThis() + "与" + currNode.getTableNameOther() + "不是1对多(或者多对多)关系,但" + currTableClass + "中" + currRelationCode + "为数组,定义不一致");
                log.warn(tableNameParent + "与" + tableNameThis + "不是1对多(或者多对多)关系,但" + currTableClass + "中" + currRelationCode + "为数组,定义不一致");
            }
            if (subEntityExists == null) {
                subEntityExists = new ArrayList<>(8);
                setMethod.invoke(parentEntity, subEntityExists);
            }
            ((Collection<MAIN_OR_SUB>) subEntityExists).add(currEntity);
        } else if (returnType.isArray()) {
            throw new MultiException("暂时不支持array类型参数:" + getMethod);
        } else {
            if (multiProperties.getCheckRelationOneOrMany() && !ClassRelationOneOrManyEnum.ONE.equals(currNode.getTableNameThisOneOrMany())) {
                throw new MultiException(tableNameParent + "与" + tableNameThis + "不是1对1(或者多对多)关系,但" + currTableClass + "中" + currRelationCode + "为对象,定义不一致");
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
