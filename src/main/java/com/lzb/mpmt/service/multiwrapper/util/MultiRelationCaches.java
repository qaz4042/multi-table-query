package com.lzb.mpmt.service.multiwrapper.util;

import com.lzb.mpmt.service.multiwrapper.annotations.MultiTableField;
import com.lzb.mpmt.service.multiwrapper.annotations.MultiTableId;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

/**
 * 表对应实体,表和表关系的缓存
 * key<String>尽可能为驼峰首字母小写格式
 *
 * @author Administrator
 */
@SuppressWarnings({"unused", "AlibabaLowerCamelCaseVariableNaming"})
public class MultiRelationCaches {

    /**
     * 表的主键(对应的Field)
     */
    private static final Map<Class<?>, Field> TABLE_CLASS_ID_FIELD_MAP = new WeakHashMap<>(4096);

    /**
     * 将resultSet数据set到实体里,需要判断数据类型
     */
    private static final Map<Class<?>, Map<String, MultiTuple2<Field, Method>>> TABLE_CLASS_FIELD_SET_METHOD_MAP = new WeakHashMap<>(4096);


    /**
     * 将子表数据,get,set到主表去
     */
    private static final Map<String, MultiTuple2<Method, Method>> TABLE_WITH_TABLE_GET_SET_METHOD_MAP = new WeakHashMap<>(4096);


    /**
     * 表中每个属性和set方法
     */
    public static Map<String, MultiTuple2<Field, Method>> getClassInfos(Class<?> tableClass) {
        return computeIfAbsentClassFieldMap(tableClass);
    }

    /**
     * 表中每个属性的set方法
     */
    public static Class<?> getFieldType(String fieldName, Class<?> tableClass) {
        Map<String, MultiTuple2<Field, Method>> map = computeIfAbsentClassFieldMap(tableClass);
        MultiTuple2<Field, Method> fieldNow = map.get(fieldName);
        MultiUtil.assertNoNull(fieldNow,"找不到{0}对应的{1}属性",tableClass,fieldName);
        return fieldNow.getT1().getType();
    }

    public static List<String> getFieldNamesByClass(Class<?> tableClass) {
        return computeIfAbsentClassFieldMap(tableClass).values().stream().map(tuple2 -> MultiUtil.camelToUnderline(tuple2.getT2().getName())).collect(Collectors.toList());
    }

    public static MultiTuple2<Method, Method> getTableWithTable_getSetMethod(String relationCode, Class<?> tableClass) {
        MultiTuple2<Method, Method> methods = TABLE_WITH_TABLE_GET_SET_METHOD_MAP.get(relationCode);
//        String fieldNameUpperFirst = MultiUtil.firstToUpperCase(MultiUtil.underlineToCamel(relationCode));
        String fieldNameUpperFirst = MultiUtil.firstToUpperCase(relationCode);
        if (methods == null) {
            //初始化map
            Method getMethod = MultiUtil.getAllMethods(tableClass).stream()
                    .filter(m -> unStaticUnFinal(m.getModifiers()))
                    .filter(m -> m.getName().equals("get" + fieldNameUpperFirst))
                    .filter(m -> !MultiUtil.isBasicDataType(m.getReturnType()))
                    .findAny().orElse(null);

            Method setMethod = MultiUtil.getAllMethods(tableClass).stream()
                    .filter(m -> unStaticUnFinal(m.getModifiers()))
                    .filter(m -> m.getName().equals("set" + fieldNameUpperFirst))
                    .filter(m -> m.getParameterTypes().length == 1 && !MultiUtil.isBasicDataType(m.getParameterTypes()[0]))
                    .findAny().orElse(null);

            MultiUtil.assertNoNull(setMethod, "找不到{0}对应的get{1}", tableClass,fieldNameUpperFirst);
            MultiUtil.assertNoNull(getMethod, "找不到{0}对应的get{1}", tableClass,fieldNameUpperFirst);

            TABLE_WITH_TABLE_GET_SET_METHOD_MAP.put(relationCode, new MultiTuple2<>(getMethod, setMethod));
        }
        return TABLE_WITH_TABLE_GET_SET_METHOD_MAP.get(relationCode);
    }


    @SneakyThrows
    public static Field getTableIdField(Class<?> tableClass) {
        Field idField = TABLE_CLASS_ID_FIELD_MAP.get(tableClass);
        if (idField == null) {
            List<Field> allFields = MultiUtil.getAllFields(tableClass);
            idField = allFields.stream().filter(f -> null != f.getAnnotation(MultiTableId.class)).findAny().orElse(null);
            if (idField == null) {
                idField = allFields.stream().filter(f -> "id".equals(f.getName())).findAny().orElse(null);
            }
            MultiUtil.assertNoNull(idField, "找不到id字段(或者@MutilTableId对应属性){0}", tableClass);
            TABLE_CLASS_ID_FIELD_MAP.put(tableClass, idField);
        }
        return idField;
    }


    private static Map<String, MultiTuple2<Field, Method>> computeIfAbsentClassFieldMap(Class<?> tableClass) {
        return TABLE_CLASS_FIELD_SET_METHOD_MAP.computeIfAbsent(tableClass, (key) -> MultiUtil.getAllFields(tableClass).stream()
                .filter(field -> unStaticUnFinal(field.getModifiers()))
                .filter(field -> MultiUtil.isBasicDataType(field.getType()))
                .filter(field -> {
                    MultiTableField anno = field.getAnnotation(MultiTableField.class);
                    return null == anno || anno.exist();
                })
                //todo
                .collect(Collectors.toMap(f -> MultiUtil.camelToUnderline(f.getName()), f -> null)));
    }

    private static boolean unStaticUnFinal(int modifiers) {
        return !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers);
    }

}
