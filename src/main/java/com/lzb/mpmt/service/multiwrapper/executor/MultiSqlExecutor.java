package com.lzb.mpmt.service.multiwrapper.executor;

import com.lzb.mpmt.service.multiwrapper.wrapper.MultiWrapper;
import com.lzb.mpmt.service.multiwrapper.wrapper.subwrapper.IMultiWrapperSubAndRelationTreeNode;
import com.lzb.mpmt.service.multiwrapper.enums.IMultiEnum;
import com.lzb.mpmt.service.multiwrapper.util.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Administrator
 */
@Slf4j
@Service
public class MultiSqlExecutor {

    private static JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        MultiSqlExecutor.jdbcTemplate = jdbcTemplate;
    }

    @SneakyThrows
    public static <MAIN> List<MAIN> query(MultiWrapper<MAIN> wrapper) {
        String sql = wrapper.computeSql();
        Map<String, Object> relationIdObjectMap = new HashMap<>(2048);
        List<MAIN> list = jdbcTemplate.query(sql, (resultSet, i) -> buildReturn(wrapper, resultSet, relationIdObjectMap))
                .stream().filter(Objects::nonNull).collect(Collectors.toList());

        log.info("Multi 查询结果{}条, sql:{}", list.size(), sql);
        return list;
    }

    @SneakyThrows
    private static <MAIN> MAIN buildReturn(MultiWrapper<MAIN> wrapper, ResultSet resultSet, Map<String, Object> relationIdObjectMap) {
        TreeNode<IMultiWrapperSubAndRelationTreeNode> relationTreeNodeTop = wrapper.getRelationTree();
        return buildReturnRecursion(null, relationTreeNodeTop, resultSet, relationIdObjectMap);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private static <MAIN_OR_SUB> MAIN_OR_SUB buildReturnRecursion(MAIN_OR_SUB parentEntity,
                                                                  TreeNode<IMultiWrapperSubAndRelationTreeNode> relationTreeNode,
                                                                  ResultSet resultSet,
                                                                  Map<String, Object> relationIdObjectMap) {
        IMultiWrapperSubAndRelationTreeNode currNode = relationTreeNode.getCurr();
        Class<?> currTableClass = currNode.getTableClassThis();
        String currRelationCode = currNode.getRelationCode();
        Field currTableIdField = MultiRelationCaches.getTableIdField(currTableClass);
        String idFieldName = currRelationCode + "." + MultiUtil.camelToUnderline(currTableIdField.getName());
        Object id = getValue(idFieldName, currTableIdField, resultSet);
        MAIN_OR_SUB currEntity = (MAIN_OR_SUB) relationIdObjectMap.get(idFieldName + "_" + id);
        //要新增元素
        if (currEntity == null) {
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
                    if (subEntityExists == null) {
                        subEntityExists = new ArrayList<>(8);
                        setMethod.invoke(parentEntity, subEntityExists);
                    }
                    ((Collection<MAIN_OR_SUB>) subEntityExists).add(currEntity);
                } else if (returnType.isArray()) {
                    throw new MultiException("暂时不支持array类型参数:" + getMethod);
                } else {
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
        return currEntity;
    }

    // todo 抽出sql查询方法提供定制
    @SneakyThrows
    private static Object getValue(String fieldName, Field field, ResultSet resultSet) {
        Class<?> type = field.getType();
        return getValue(fieldName, type, resultSet);
    }

    private static Object getValue(String fieldName, Class<?> type, ResultSet resultSet) throws SQLException {
        if (Long.class.isAssignableFrom(type)) {
            return resultSet.getLong(fieldName);
        }
        if (Integer.class.isAssignableFrom(type)) {
            return resultSet.getInt(fieldName);
        }
        if (Float.class.isAssignableFrom(type)) {
            return resultSet.getFloat(fieldName);
        }
        if (Double.class.isAssignableFrom(type)) {
            return resultSet.getDouble(fieldName);
        }
        if (String.class.isAssignableFrom(type)) {
            return resultSet.getString(fieldName);
        }
        if (BigDecimal.class.isAssignableFrom(type)) {
            return resultSet.getBigDecimal(fieldName);
        }
        if (Blob.class.isAssignableFrom(type)) {
            return resultSet.getBlob(fieldName);
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return resultSet.getBoolean(fieldName);
        }
        if (Date.class.isAssignableFrom(type)) {
            return resultSet.getDate(fieldName);
        }
        if (LocalDateTime.class.isAssignableFrom(type)) {
            return MultiUtil.date2LocalDateTime(resultSet.getDate(fieldName));
        }
        if (LocalDate.class.isAssignableFrom(type)) {
            return MultiUtil.date2LocalDateTime(resultSet.getDate(fieldName)).toLocalDate();
        }
        if (LocalTime.class.isAssignableFrom(type)) {
            return MultiUtil.date2LocalDateTime(resultSet.getDate(fieldName)).toLocalTime();
        }
        if (Enum.class.isAssignableFrom(type)) {
            if (IMultiEnum.class.isAssignableFrom(type)) {
                Integer value = resultSet.getInt(fieldName);
                return MultiUtil.getEnumByValue(type, value);
            } else {
                //默认用枚举的name存取
                String value = resultSet.getString(fieldName);
                return MultiUtil.getEnumByName(type, value);
            }
        }
        throw new MultiException("未知的数据类型|" + fieldName + "|" + type);
    }
}
