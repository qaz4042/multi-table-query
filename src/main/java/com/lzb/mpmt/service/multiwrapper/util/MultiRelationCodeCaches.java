package com.lzb.mpmt.service.multiwrapper.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * RelationCode相关缓存
 */
@SuppressWarnings({"unused", "AlibabaLowerCamelCaseVariableNaming"})
public class MultiRelationCodeCaches {
    /**
     * 将resultSet数据set到表里
     */
    Map<String, Method> relation_setMethodMap;

    /**
     * 将resultSet数据set到表里,需要判断数据类型
     */
    Map<String, Type> relation_fieldTypeMap;

    /**
     * 将子表数据,set到主表去
     */
    Map<String, Method> relation_TableWithTable_setMethodMap;

    /**
     * 判断表跟表是否是一对一,一对多关系
     */
    Map<String, Type> relation_TableWithTable_fieldTypeMap;

    public Map<String, Method> getRelation_setMethodMap() {
        return relation_setMethodMap;
    }

    public Map<String, Type> getRelation_fieldTypeMap() {
        return relation_fieldTypeMap;
    }

    public Map<String, Method> getRelation_TableWithTable_setMethodMap() {
        return relation_TableWithTable_setMethodMap;
    }

    public Map<String, Type> getRelation_TableWithTable_fieldTypeMap() {
        return relation_TableWithTable_fieldTypeMap;
    }
}
