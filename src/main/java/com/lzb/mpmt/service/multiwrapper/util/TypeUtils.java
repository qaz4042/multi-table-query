package com.lzb.mpmt.service.multiwrapper.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class TypeUtils {
    /**
     * 即boolean 、byte、char、short、int、long、float和double
     *
     * @seeClass.isPrimitive()
     */

    public static boolean isPrimitiveType(Class clazz) {
        return clazz != null && clazz.isPrimitive();
    }

    public static boolean isPrimitiveWrapperType(Class clazz) {
        //抽象类Number是BigDecimal、BigInteger、Byte、Double、Float、Integer、Long和Short类的超类
//        return clazz != null;
        return Number.class.isAssignableFrom(clazz)
                || Boolean.class.isAssignableFrom(clazz)
                || Byte.class.isAssignableFrom(clazz)
                || Character.class.isAssignableFrom(clazz)
                ;
    }


    public static boolean isStringType(Class clazz) {
        return false;
    }

    public static boolean isArray(Class clazz) {
        return clazz != null && clazz.isArray();
    }

    public static boolean isMap(Class clazz) {

        return isContainType(clazz, Map.class);

    }

    public static boolean isSet(Class clazz) {

        return isContainType(clazz, Set.class);

    }

    public static boolean isList(Class clazz) {

        return isContainType(clazz, List.class);

    }

    public static boolean isCollection(Class clazz) {
        return isContainType(clazz, Collection.class);

    }

    private static boolean isContainType(Class clazz, Class cClazz) {
        return cClazz.isAssignableFrom(clazz);
    }

    /**
     * 简单类型:原始类型，原始类型的包装类及String类型都归类为
     */

    public static boolean isSimpleType(Class clazz) {
        return isStringType(clazz) || isPrimitiveWrapperType(clazz) || isPrimitiveType(clazz);

    }

    /**
     * 复合类型：简单类型，集合，数组，Map除外
     */

    public static boolean isComplexType(Class clazz) {

        return !(isSimpleType(clazz) || isMap(clazz) || isList(clazz) || isSet(clazz) || isArray(clazz));

    }
}
