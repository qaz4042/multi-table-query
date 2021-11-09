package com.lzb.mpmt.service.multiwrapper.jdbc;

import com.lzb.mpmt.service.*;
import com.lzb.mpmt.service.multiwrapper.util.MultiException;
import com.lzb.mpmt.service.multiwrapper.util.MultiRelationCaches;
import com.lzb.mpmt.service.multiwrapper.util.TreeNode;
import com.lzb.mpmt.service.multiwrapper.util.Tuple2;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class MysqlExecutor {

    private static JdbcTemplate jdbcTemplate;

    @Autowired    // 自动注入，spring boot会帮我们实例化一个对象
    public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        MysqlExecutor.jdbcTemplate = jdbcTemplate;
    }

    @SneakyThrows
    public static <MAIN extends MultiModel> List<MAIN> query(MultiWrapper<MAIN> wrapper) {
        String sql = wrapper.computeSql();
        Map<String, Object> relationIdObjectMap = new HashMap<>(2048);
        return jdbcTemplate.query(sql, (resultSet, i) -> buildReturn(wrapper, resultSet, relationIdObjectMap))
                .stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @SneakyThrows
    private static <MAIN extends MultiModel> MAIN buildReturn(MultiWrapper<MAIN> wrapper, ResultSet resultSet, Map<String, Object> relationIdObjectMap) {
        TreeNode<IMultiWrapperSubAndRelationTreeNode> relationTreeNodeTop = wrapper.getRelationTree();
        return buildReturnRecursion(null, relationTreeNodeTop, resultSet, relationIdObjectMap);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private static <MAIN_OR_SUB extends MultiModel> MAIN_OR_SUB buildReturnRecursion(MAIN_OR_SUB parentEntity,
                                                                                     TreeNode<IMultiWrapperSubAndRelationTreeNode> relationTreeNode,
                                                                                     ResultSet resultSet,
                                                                                     Map<String, Object> relationIdObjectMap) {
        IMultiWrapperSubAndRelationTreeNode currNode = relationTreeNode.getCurr();
        Class<?> currTableClass = currNode.getTableClassThis();
        String currRelationCode = currNode.getRelationCode();
        Field currTableIdField = MultiRelationCaches.getTableIdField(currTableClass);
        Object id = getValue(currTableIdField.getName(), currTableIdField, resultSet);
        MAIN_OR_SUB currEntity = (MAIN_OR_SUB) relationIdObjectMap.get(currRelationCode + id);
        //要新增元素
        if (currEntity == null) {
//            //如果是主表
//            MultiWrapperSubAndRelationTreeNodeMain currMain = currNode instanceof MultiWrapperSubAndRelationTreeNodeMain ? ((MultiWrapperSubAndRelationTreeNodeMain) currNode) : null;
//            if (currMain != null) {
//            }
//            //如果是子表
//            MultiWrapperSubAndRelation currRelation = currNode instanceof MultiWrapperSubAndRelation ? ((MultiWrapperSubAndRelation) currNode) : null;
//            if (currRelation != null) {
//
//            }
            //重复则不在生成
            currEntity = (MAIN_OR_SUB) currTableClass.newInstance();
            List<String> selectFields = currNode.getMultiWrapperSelectInfo().getSelectFields();
            for (String selectField : selectFields) {
                Class<?> fieldReturnType = MultiRelationCaches.getRelation_fieldType(currRelationCode, selectField, currTableClass);
                MultiRelationCaches.getRelation_setMethod(selectField, currTableClass).invoke(currEntity, getValue(selectField, fieldReturnType, resultSet));
            }

            //顶层节点为null,不用出setSubEntitys(subEntitys);
            if (parentEntity != null) {
                Tuple2<Method, Method> getSetMethods = MultiRelationCaches.getRelation_TableWithTable_getSetMethod(currRelationCode, parentEntity.getClass());
                Method getMethod = getSetMethods.getT1();
                Method setMethod = getSetMethods.getT2();
                Object subEntityExists = getMethod.invoke(parentEntity);
                Class<?> returnType = getMethod.getReturnType();
                //列表
                if (Collection.class.isAssignableFrom(returnType)) {
                    if (subEntityExists == null) {
                        subEntityExists = returnType.newInstance();
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

        //副表信息,要递推填充下去
        MAIN_OR_SUB finalCurrEntity = currEntity;
        relationTreeNode.getChildren().forEach(subNode -> {
            buildReturnRecursion(finalCurrEntity, subNode, resultSet, relationIdObjectMap);
        });
        return currEntity;
    }

    private static boolean isCheckAndSetRepeat(ResultSet resultSet, Set<String> relationIdSet, String tableName, Field idField, String idFieldFullName) {
        if (null == idField) {
            //没重复
            return false;
        }
        String relationIdVale = tableName + getValue(idFieldFullName, idField, resultSet);
        boolean repeat = relationIdSet.contains(relationIdVale);
        if (!repeat) {
            //没重复,本次添加到结果集以后,下次就重复
            relationIdSet.add(relationIdVale);
        }
        return repeat;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private static <T> T getValue(String fieldName, Field field, ResultSet resultSet) {
        Class<?> type = field.getType();
        return getValue(fieldName, type, resultSet);
    }

    private static <T> T getValue(String fieldName, Class<?> type, ResultSet resultSet) throws SQLException {
        if (Long.class.isAssignableFrom(type)) {
            return (T) (Long) resultSet.getLong(fieldName);
        }
        if (Integer.class.isAssignableFrom(type)) {
            return (T) (Integer) resultSet.getInt(fieldName);
        }
        if (String.class.isAssignableFrom(type)) {
            return (T) resultSet.getString(fieldName);
        }
        //todo 其他类型
        return null;
    }
}
