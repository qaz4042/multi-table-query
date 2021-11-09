package com.lzb.mpmt.service.multiwrapper.util;

import com.lzb.mpmt.service.multiwrapper.annotations.MutilTableId;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
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
    private static final Map<String, Class<?>> relation_fieldTypeMap = new WeakHashMap<>(4096);

    /**
     * 将子表数据,get,set到主表去
     */
    private static final Map<String, Tuple2<Method, Method>> relation_TableWithTable_getSetMethodMap = new WeakHashMap<>(4096);

    /**
     * 判断表跟表是否是一对一,一对多关系
     */
    private static final Map<String, Type> relation_TableWithTable_fieldTypeMap = new WeakHashMap<>(4096);

    /**
     * 各个表的主键
     */
    private static final Map<Class<?>, Field> tableIdFieldMap = new WeakHashMap<>(4096);

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

    public static Class<?> getRelation_fieldType(String relationCode, String fieldName, Class<?> tableClass) {
        String relationCodeFieldName= MultiUtil.firstToLowerCase(fieldName);
        Class<?> type = relation_fieldTypeMap.get(relationCodeFieldName);
        if (type == null) {
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
        return type;
    }


    public static Tuple2<Method, Method> getRelation_TableWithTable_getSetMethod(String relationCode, Class<?> tableClass) {
        Tuple2<Method, Method> methods = relation_TableWithTable_getSetMethodMap.get(relationCode);
        if (methods == null) {
            //初始化map
            Method setMethod = Arrays.stream(tableClass.getDeclaredMethods())
                    .filter(m -> publicNoStaticFinal(m.getModifiers()))
                    .filter(m -> m.getName().equals("set" + MultiUtil.firstToUpperCase(relationCode)))
                    .findAny().orElse(null);

            Method getMethod = Arrays.stream(tableClass.getDeclaredMethods())
                    .filter(m -> publicNoStaticFinal(m.getModifiers()))
                    .filter(m -> m.getName().equals("get" + MultiUtil.firstToUpperCase(relationCode)))
                    .findAny().orElse(null);
            relation_TableWithTable_getSetMethodMap.put(relationCode, new Tuple2<>(getMethod, setMethod));
        }
        methods = relation_TableWithTable_getSetMethodMap.get(relationCode);
        if (methods == null) {
            //todo 可能在这里抛异常不太合适
            throw new MultiException("找不到" + relationCode + "对应的get,set" + MultiUtil.firstToUpperCase(getPropName(relationCode)));
        }
        return methods;
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

    @SneakyThrows
    public static Field getTableIdField(Class<?> tableClass) {
        Field idField = tableIdFieldMap.get(tableClass);
        if (idField == null) {
            idField = Arrays.stream(tableClass.getDeclaredFields()).filter(f -> null != f.getAnnotation(MutilTableId.class)).findAny().orElse(null);
            if (idField == null) {
                idField = Arrays.stream(tableClass.getDeclaredFields()).filter(f -> "id".equals(f.getName())).findAny().orElse(null);
                ;
            }
            if (idField == null) {
                throw new MultiException("找不到id字段(或者@MutilTableId对应属性)" + tableClass);
            }
            tableIdFieldMap.put(tableClass, idField);
        }
        return idField;
    }

    private static boolean publicNoStaticFinal(int modifiers) {
        return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers);
    }

    private static String getPropName(String relationCodeFieldName) {
        return MultiUtil.underlineToCamel(relationCodeFieldName.split(",")[1]);
    }
}
