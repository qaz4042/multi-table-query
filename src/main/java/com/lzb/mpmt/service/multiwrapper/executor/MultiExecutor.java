package com.lzb.mpmt.service.multiwrapper.executor;

import com.lzb.mpmt.service.multiwrapper.config.MultiProperties;
import com.lzb.mpmt.service.multiwrapper.enums.ClassRelationOneOrManyEnum;
import com.lzb.mpmt.service.multiwrapper.enums.IMultiEnum;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.IMultiSqlExecutor;
import com.lzb.mpmt.service.multiwrapper.util.*;
import com.lzb.mpmt.service.multiwrapper.wrapper.MultiWrapper;
import com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent.IMultiWrapperSubAndRelationTreeNode;
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
            Tuple2<MAIN, Boolean> mainAndIsNew = buildReturnRecursion(null, wrapper.getRelationTree(), resultSet, relationIdObjectMap);
            MAIN main = mainAndIsNew.getT1();
            return mainAndIsNew.getT2() ? main : null;
        }).stream().filter(Objects::nonNull).collect(Collectors.toList());

        if (multiProperties.getCheckRelationRequire()) {
            //检查表关系中,一方有数据,另一方必须有数据,是否有异常数据(测试环境可以开启) MultiTableRelation relation = MultiWrapperSubAndRelation.MULTI_TABLE_RELATION_FACTORY.getRelationCodeMap().get(currNode.getRelationCode());
            checkRequire(mains, wrapper);
        }

        log.info("Multi 查询结果{}条, sql:{}", mains.size(), sql);
        return mains;
    }

    private static <MAIN> void checkRequire(List<MAIN> mains, MultiWrapper<MAIN> wrapper) {

    }

//    @SneakyThrows
//    public static <MAIN> List<MAIN> page(MultiPageOrder page, MultiWrapper<MAIN> wrapper) {
//    }

    /**
     * 递归构造子表对象
     *
     * @param parentEntity        父表对象
     * @param relationTreeNode    父表和子表当前的关系(.chlidren()是子表和他的子表的关系)
     * @param resultSet           sql执行后的每一行结果集
     * @param relationIdObjectMap 已经构造好的对象,如果有id,需要按id合并子表信息到list中
     * @param <MAIN_OR_SUB>       父表对象的泛型
     * @return 新的父表对象信息(旧的副表对象信息)
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows
    private static <MAIN_OR_SUB> Tuple2<MAIN_OR_SUB, Boolean> buildReturnRecursion(MAIN_OR_SUB parentEntity,
                                                                                   TreeNode<IMultiWrapperSubAndRelationTreeNode> relationTreeNode,
                                                                                   ResultSet resultSet,
                                                                                   Map<String, Object> relationIdObjectMap) {
        IMultiWrapperSubAndRelationTreeNode currNode = relationTreeNode.getCurr();
        Class<?> currTableClass = currNode.getTableClassThis();
        String currRelationCode = currNode.getRelationCode();
        Field currTableIdField = MultiRelationCaches.getTableIdField(currTableClass);
        String idFieldName = currRelationCode + "." + MultiUtil.camelToUnderline(currTableIdField.getName());
        Object id = getValue(idFieldName, currTableIdField.getType(), resultSet);
        MAIN_OR_SUB currEntity = (MAIN_OR_SUB) relationIdObjectMap.get(idFieldName + "_" + id);
        //要新增元素
        boolean isNew = currEntity == null;
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
                Tuple2<Method, Method> getSetMethods = MultiRelationCaches.getRelation_TableWithTable_getSetMethod(currRelationCode, parentEntity.getClass());
                Method getMethod = getSetMethods.getT1();
                Method setMethod = getSetMethods.getT2();
                Object subEntityExists = getMethod.invoke(parentEntity);
                Class<?> returnType = getMethod.getReturnType();

                //列表
                if (List.class.isAssignableFrom(returnType)) {
                    if (!ClassRelationOneOrManyEnum.MANY.equals(currNode.getSubTableOneOrMany())) {
//                        throw new MultiException(currNode.getTableNameThis() + "与" + currNode.getTableNameOther() + "不是1对多(或者多对对)关系,但" + currTableClass + "中" + currRelationCode + "为数组,定义不一致");
                        log.warn(currNode.getTableNameThis() + "与" + currNode.getTableNameOther() + "不是1对多(或者多对对)关系,但" + currTableClass + "中" + currRelationCode + "为数组,定义不一致");
                    }
                    if (subEntityExists == null) {
                        subEntityExists = new ArrayList<>(8);
                        setMethod.invoke(parentEntity, subEntityExists);
                    }
                    ((Collection<MAIN_OR_SUB>) subEntityExists).add(currEntity);
                } else if (returnType.isArray()) {
                    throw new MultiException("暂时不支持array类型参数:" + getMethod);
                } else {
                    if (multiProperties.getCheckRelationOneOrMany() && !ClassRelationOneOrManyEnum.MANY.equals(currNode.getSubTableOneOrMany())) {
                        throw new MultiException(currNode.getTableNameThis() + "与" + currNode.getTableNameOther() + "不是1对1(或者多对对)关系,但" + currTableClass + "中" + currRelationCode + "为对象,定义不一致");
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
        }

        relationIdObjectMap.put(idFieldName + "_" + id, currEntity);
        //副表信息,要递推填充下去
        MAIN_OR_SUB finalCurrEntity = currEntity;
        relationTreeNode.getChildren().forEach(subNode -> buildReturnRecursion(finalCurrEntity, subNode, resultSet, relationIdObjectMap));
        return new Tuple2<>(currEntity, isNew);
    }

    @SneakyThrows
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object getValue(String fieldName, Class<?> type, ResultSet resultSet) {
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
