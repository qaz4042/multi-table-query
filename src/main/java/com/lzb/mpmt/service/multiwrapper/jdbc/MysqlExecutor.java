package com.lzb.mpmt.service.multiwrapper.jdbc;

import com.lzb.mpmt.service.IMultiWrapperSubAndRelationTreeNode;
import com.lzb.mpmt.service.MultiModel;
import com.lzb.mpmt.service.MultiWrapper;
import com.lzb.mpmt.service.MultiWrapperSubAndRelation;
import com.lzb.mpmt.service.multiwrapper.util.TreeNode;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

public class MysqlExecutor {

    private static JdbcTemplate jdbcTemplate;

    @Autowired    // 自动注入，spring boot会帮我们实例化一个对象
    public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        MysqlExecutor.jdbcTemplate = jdbcTemplate;
    }

    @SneakyThrows
    public static <MAIN extends MultiModel> List<MAIN> query(MultiWrapper<MAIN> wrapper) {
        String sql = wrapper.computeSql();
        Set<String> relationIdSet = new HashSet<>(2048);
        return jdbcTemplate.query(sql, (resultSet, i) -> buildReturn(wrapper, resultSet, relationIdSet))
                .stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    //todo
    private static Map<String, Method> fieldSetMethodMap = new WeakHashMap<>();

    @SneakyThrows
    private static <MAIN extends MultiModel> MAIN buildReturn(MultiWrapper<MAIN> wrapper, ResultSet resultSet, Set<String> relationIdSet) {
        String tableName = wrapper.getWrapperMain().getTableName();
        Field idField = wrapper.getWrapperMain().getIdField();
        String idFieldName = wrapper.getWrapperMain().getIdFieldName();
        boolean checkAndSetIsRepeat = isCheckAndSetRepeat(resultSet, relationIdSet, tableName, idField, idFieldName);
        boolean isNewObj = !checkAndSetIsRepeat;
        MAIN main = null;
        if (isNewObj) {
            //重复则不在生成
            main = wrapper.getWrapperMain().getClazz().newInstance();
            for (String selectField : wrapper.getWrapperMain().getSelectFields()) {
                Field field = null; //todo 提前确定出field
                fieldSetMethodMap.get(selectField).invoke(main, getValue(selectField, field, resultSet));
            }
        } else {
//            旧的列表中查询出来
        }

        //(子类信息还要 找到原来那条去聚合) todo
        //第一个是当前主表
        wrapper.getRelationTree().getChildren().forEach(relationTreeNode -> {
            Object sub = buildReturnRecursion(relationTreeNode, resultSet, relationIdSet, isNewObj);
            //看是否添加到列表
//            main;//把 listSub set到main里面
        });
        return main;
    }

    private static <SUB> SUB buildReturnRecursion(TreeNode<IMultiWrapperSubAndRelationTreeNode> relationTreeNode, ResultSet resultSet, Set<String> relationIdSet, Boolean isParentNewObj) {
        MultiWrapperSubAndRelation curr = (MultiWrapperSubAndRelation) relationTreeNode.getCurr();
        String relationCode = curr.getRelationCode();
        Field idField = curr.getWrapperSub().getIdField();
        String idFieldName = curr.getWrapperSub().getIdFieldName();
//        relationTreeNode.getChildren().forEach(relationTreeNode -> {
//            Object subChild = buildReturnRecursion(relationTreeNode, resultSet, relationIdSet, isNewObj);
//            //看是否添加到列表
//            main;//把 listSub set到main里面
//        });
        return null;
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
