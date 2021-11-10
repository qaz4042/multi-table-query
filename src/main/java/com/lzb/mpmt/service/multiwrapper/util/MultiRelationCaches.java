package com.lzb.mpmt.service.multiwrapper.util;

import com.lzb.mpmt.service.multiwrapper.annotations.MultiTableField;
import com.lzb.mpmt.service.multiwrapper.annotations.MultiTableId;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

/**
 * RelationCode相关缓存
 *
 * @author Administrator
 */
@SuppressWarnings({"unused", "AlibabaLowerCamelCaseVariableNaming"})
public class MultiRelationCaches {

    /**
     * 表的主键(对应的Field)
     */
    private static final Map<Class<?>, Field> tableClass_IdFieldMap = new WeakHashMap<>(4096);

    /**
     * 表的每个字段名
     */
    private static final Map<Class<?>, List<String>> tableClass_fieldNamesMap = new WeakHashMap<>(4096);

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


    public static Method getRelation_setMethod(String relationCode, String fieldName, Class<?> tableClass) {
        String relationCodeFieldName = relationCode + "." + fieldName;
        Method method = relation_setMethodMap.get(relationCodeFieldName);
        if (method == null) {
            //初始化map
            MultiUtil.getAllMethods(tableClass).stream()
                    .filter(field -> unStaticUnFinal(field.getModifiers()))
                    .filter(m -> m.getName().startsWith("set"))
                    .filter(m -> m.getParameterTypes().length == 1 && MultiUtil.isBasicType(m.getParameterTypes()[0]))
                    .forEach(m -> {
                        relation_setMethodMap.put(relationCode + "." + MultiUtil.methodNameToFieldName(m.getName()), m);
                    });
        }
        method = relation_setMethodMap.get(relationCodeFieldName);
        if (method == null) {
            throw new MultiException("找不到" + tableClass + "对应的set" + MultiUtil.firstToUpperCase(MultiUtil.underlineToCamel(fieldName)));
        }
        return method;
    }

    public static Class<?> getRelation_fieldType(String relationCode, String fieldName, Class<?> tableClass) {
        String relationCodeFieldName = relationCode + "." + fieldName;
        Class<?> type = relation_fieldTypeMap.get(relationCodeFieldName);
        if (type == null) {
            //初始化map
            tableClass_fieldNamesMap.put(tableClass, MultiUtil.getAllFields(tableClass).stream()
                    .filter(field -> unStaticUnFinal(field.getModifiers()))
                    .filter(field -> MultiUtil.isBasicType(field.getType()))
                    .filter(field -> {
                        MultiTableField anno = field.getAnnotation(MultiTableField.class);
                        return null == anno || anno.exist();
                    })
                    .peek(field -> relation_fieldTypeMap.put(relationCode + "." + MultiUtil.camelToUnderline(field.getName()), field.getType()))
                    .map(Field::getName)
                    .collect(Collectors.toList()));
        }
        type = relation_fieldTypeMap.get(relationCodeFieldName);
        if (type == null) {
            throw new MultiException("找不到" + tableClass + "对应的" + fieldName + "属性");
        }
        return type;
    }

    public static List<String> getFieldNamesByClass(Class<?> tableClass) {
        List<String> fields = tableClass_fieldNamesMap.get(tableClass);
        if (fields == null) {
            //初始化map
            tableClass_fieldNamesMap.put(tableClass, MultiUtil.getAllFields(tableClass).stream()
                    .filter(field -> unStaticUnFinal(field.getModifiers()))
                    .filter(field -> MultiUtil.isBasicType(field.getType()))
                    .filter(field -> {
                        MultiTableField anno = field.getAnnotation(MultiTableField.class);
                        return null == anno || anno.exist();
                    }).map(field->MultiUtil.camelToUnderline(field.getName())).collect(Collectors.toList()));
        }
        fields = tableClass_fieldNamesMap.get(tableClass);
        return fields;
    }


    public static Tuple2<Method, Method> getRelation_TableWithTable_getSetMethod(String relationCode, Class<?> tableClass) {
        Tuple2<Method, Method> methods = relation_TableWithTable_getSetMethodMap.get(relationCode);
        String fieldNameUpperFirst = MultiUtil.firstToUpperCase(MultiUtil.underlineToCamel(relationCode));
        if (methods == null) {
            //初始化map
            Method setMethod = MultiUtil.getAllMethods(tableClass).stream()
                    .filter(m -> unStaticUnFinal(m.getModifiers()))
                    .filter(m -> m.getName().equals("set" + fieldNameUpperFirst))
                    .filter(m -> m.getParameterTypes().length == 1 && !MultiUtil.isBasicType(m.getParameterTypes()[0]))
                    .findAny().orElse(null);

            Method getMethod = MultiUtil.getAllMethods(tableClass).stream()
                    .filter(m -> unStaticUnFinal(m.getModifiers()))
                    .filter(m -> m.getName().equals("get" + fieldNameUpperFirst))
                    .filter(m -> !MultiUtil.isBasicType(m.getReturnType()))
                    .findAny().orElse(null);
            if (setMethod == null) {
                throw new MultiException("找不到" + tableClass + "对应的set" + fieldNameUpperFirst);
            }
            if (getMethod == null) {
                throw new MultiException("找不到" + tableClass + "对应的get" + fieldNameUpperFirst);
            }
            relation_TableWithTable_getSetMethodMap.put(relationCode, new Tuple2<>(getMethod, setMethod));
        }
        methods = relation_TableWithTable_getSetMethodMap.get(relationCode);

        return methods;
    }

    public static Type getRelation_TableWithTable_fieldType(String relationCode, Class<?> tableClass) {
        Type type = relation_TableWithTable_fieldTypeMap.get(relationCode);
        if (type == null) {
            //初始化map
            MultiUtil.getAllFields(tableClass).stream()
                    .filter(field -> unStaticUnFinal(field.getModifiers()))
                    .filter(field -> field.getName().equals(relationCode))
                    .filter(field -> !MultiUtil.isBasicType(field.getType()))
                    .forEach(field -> relation_TableWithTable_fieldTypeMap.put(relationCode + "." + MultiUtil.camelToUnderline(field.getName()), field.getType()));
        }
        type = relation_TableWithTable_fieldTypeMap.get(relationCode);
        if (type == null) {
            throw new MultiException("找不到" + relationCode + "对应的set" + MultiUtil.firstToUpperCase(relationCode.split(",")[1]));
        }
        return type;
    }

    @SneakyThrows
    public static Field getTableIdField(Class<?> tableClass) {
        Field idField = tableClass_IdFieldMap.get(tableClass);
        if (idField == null) {
            List<Field> allFields = MultiUtil.getAllFields(tableClass);
            idField = allFields.stream().filter(f -> null != f.getAnnotation(MultiTableId.class)).findAny().orElse(null);
            if (idField == null) {
                idField = allFields.stream().filter(f -> "id".equals(f.getName())).findAny().orElse(null);
            }
            if (idField == null) {
                throw new MultiException("找不到id字段(或者@MutilTableId对应属性)" + tableClass);
            }
            tableClass_IdFieldMap.put(tableClass, idField);
        }
        return idField;
    }

    private static boolean unStaticUnFinal(int modifiers) {
        return !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers);
    }

    private static String getPropName(String relationCodeFieldName) {
        return MultiUtil.underlineToCamel(relationCodeFieldName.split(",")[1]);
    }
}