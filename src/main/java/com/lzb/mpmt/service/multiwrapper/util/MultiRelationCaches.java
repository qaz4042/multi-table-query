package com.lzb.mpmt.service.multiwrapper.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * RelationCode相关缓存
 *
 * @author Administrator
 */
@SuppressWarnings({"unused", "AlibabaLowerCamelCaseVariableNaming"})
public class MultiRelationCaches {
    /**
     * 将resultSet数据set到表里
     */
    private static final Map<String, Method> relation_setMethodMap = new WeakHashMap<>(2048);

    /**
     * 将resultSet数据set到表里,需要判断数据类型
     */
    private static final Map<String, Type> relation_fieldTypeMap = new WeakHashMap<>(2048);

    /**
     * 将子表数据,set到主表去
     */
    private static final Map<String, Method> relation_TableWithTable_setMethodMap = new WeakHashMap<>(2048);

    /**
     * 判断表跟表是否是一对一,一对多关系
     */
    private static final Map<String, Type> relation_TableWithTable_fieldTypeMap = new WeakHashMap<>(2048);

    public static Method getRelation_setMethod(String relationCodeFieldName, Class<?> tableClass) {
        Method method = relation_setMethodMap.get(relationCodeFieldName);
        if (method == null) {
            String relationCode = relationCodeFieldName.split(",")[0];
            //初始化map
            Arrays.stream(tableClass.getDeclaredMethods())
                    .filter(field -> publicNoStaticFinal(field.getModifiers()))
                    .filter(m -> m.getName().startsWith("set"))
                    .forEach(m -> {
                        relation_setMethodMap.put(relationCode + "." + MultiUtil.firstToLowerCase(m.getName().substring(3)), m);
                    });
        }
        method = relation_setMethodMap.get(relationCodeFieldName);
        if (method == null) {
            //todo 可能在这里抛异常不太合适
            throw new MultiException("找不到" + relationCodeFieldName + "对应的set" + MultiUtil.firstToUpperCase(getPropName(relationCodeFieldName)));
        }
        return method;
    }

    public static Map<String, Type> getRelation_fieldType(String relationCodeFieldName, Class<?> tableClass) {
        Type type = relation_fieldTypeMap.get(relationCodeFieldName);
        if (type == null) {
            String relationCode = relationCodeFieldName.split(",")[0];
            //初始化map
            Arrays.stream(tableClass.getDeclaredFields())
                    .filter(field -> publicNoStaticFinal(field.getModifiers()))
                    .forEach(field -> {
                        relation_fieldTypeMap.put(relationCode + "." + MultiUtil.firstToLowerCase(field.getName().substring(3)), field.getType());
                    });
        }
        type = relation_fieldTypeMap.get(relationCodeFieldName);
        if (type == null) {
            //todo 可能在这里抛异常不太合适
            throw new MultiException("找不到" + relationCodeFieldName + "对应的" + getPropName(relationCodeFieldName) + "属性");
        }
        return relation_fieldTypeMap;
    }


    public static Method getRelation_TableWithTable_setMethod(String relationCode, Class<?> tableClass) {
        Method method = relation_TableWithTable_setMethodMap.get(relationCode);
        if (method == null) {
            //初始化map
            Arrays.stream(tableClass.getDeclaredFields())
                    .filter(field -> publicNoStaticFinal(field.getModifiers()))
                    .filter(m -> m.getName().equals(relationCode))
                    .forEach(field -> relation_TableWithTable_setMethodMap.put(relationCode + "." + MultiUtil.firstToLowerCase(field.getName().substring(3)), field.getType()));
        }
        method = relation_TableWithTable_setMethodMap.get(relationCode);
        if (method == null) {
            //todo 可能在这里抛异常不太合适
            throw new MultiException("找不到" + relationCode + "对应的set" + MultiUtil.firstToUpperCase(getPropName(relationCode)));
        }
        return method;
    }

    public static Type getRelation_TableWithTable_fieldType(String relationCode, Class<?> tableClass) {
        Type type = relation_TableWithTable_fieldTypeMap.get(relationCode);
        if (type == null) {
            //初始化map
            Arrays.stream(tableClass.getDeclaredFields())
                    .filter(field -> publicNoStaticFinal(field.getModifiers()))
                    .filter(m -> m.getName().equals(relationCode))
                    .forEach(field -> relation_TableWithTable_fieldTypeMap.put(relationCode + "." + MultiUtil.firstToLowerCase(field.getName().substring(3)), field.getType()));
        }
        type = relation_TableWithTable_fieldTypeMap.get(relationCode);
        if (type == null) {
            throw new MultiException("找不到" + relationCode + "对应的set" + MultiUtil.firstToUpperCase(relationCode.split(",")[1]));
        }
        return type;
    }

    private static boolean publicNoStaticFinal(int modifiers) {
        return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers);
    }

    private static String getPropName(String relationCodeFieldName) {
        return MultiUtil.underlineToCamel(relationCodeFieldName.split(",")[1]);
    }
}
