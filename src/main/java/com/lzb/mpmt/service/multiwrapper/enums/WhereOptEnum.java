package com.lzb.mpmt.service.multiwrapper.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.lzb.mpmt.service.multiwrapper.enums.WhereOptEnum.Const.POINT;

@SuppressWarnings("ConstantConditions")
@Getter
@AllArgsConstructor
public enum WhereOptEnum {
    /***/
    eq((key, val) -> key + Const.EQ + POINT + val.toString() + POINT, "age = 18"),
    in((key, val) -> whereSqlValueList(key, val, "in"), "age in (18,19)"),
    not_in((key, val) -> whereSqlValueList(key, val, "not in"), "age not in (18,19)"),
    gt((key, val) -> key + Const.GT + POINT + val + POINT, "age > 18"),
    ge((key, val) -> key + Const.GT + Const.EQ + POINT + val + POINT, "age >= 18"),
    lt((key, val) -> key + Const.LT + POINT + val + POINT, "age < 18"),
    le((key, val) -> key + Const.LT + Const.EQ + POINT + val + POINT, "age <= 18"),
    likeDefault((key, val) -> key + Const.BLANK + Const.LIKE + Const.BLANK + POINT + "%" + val + "%" + POINT, "name like %咔咔%"),
    ;

    private final BiFunction<String, Object, String> sqlFunction;
    private final String demo;

    static class Const {
        public static final char BLANK = ' ';
        public static final char POINT = '\'';
        public static final char EQ = '=';
        public static final char GT = '>';
        public static final char LT = '<';
        public static final String LIKE = "like";
    }

    private static String whereSqlValueList(String key, Object val, String inOpt) {
        if (null == val) {
            throw new RuntimeException("in 语句的values不能为空");
        }
        if (val instanceof Collection) {
            val = ((Collection<?>) val).stream().filter(Objects::nonNull).map(v -> POINT + v.toString() + POINT).collect(Collectors.joining(","));
            if (((Collection<?>) val).size() == 0) {
                return "1!=1";
            }
        }
        if (val.getClass().isArray()) {
            if (((Object[]) val).length == 0) {
                return "1!=1";
            }
            val = Arrays.stream(((Object[]) val)).filter(Objects::nonNull).map(v -> POINT + v.toString() + POINT).collect(Collectors.joining(","));
        }
        return key + " " + inOpt + " (" + val + ")";
    }
}