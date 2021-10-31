package com.lzb.mpmt.service.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.lzb.mpmt.service.common.WhereOptEnum.Const.POINT;

@Getter
@AllArgsConstructor
public enum WhereOptEnum {
    eq((key, val) -> key + Const.eq            + POINT + val.toString() + POINT, "age = 18"),
    in((key, val) -> whereSqlValueList(key, val, "in"), "age in (18,19)"),
    not_in((key, val) -> whereSqlValueList(key, val, "not in"), "age not in (18,19)"),
    gt((key, val) -> key + Const.gt            + POINT + val + POINT, "age > 18"),
    ge((key, val) -> key + Const.gt + Const.eq + POINT + val + POINT, "age >= 18"),
    lt((key, val) -> key + Const.lt            + POINT + val + POINT, "age < 18"),
    le((key, val) -> key + Const.lt + Const.eq + POINT + val + POINT, "age <= 18"),
    likeDefault((key, val) -> key + " like " + POINT + "%" + val + POINT + "%", "name like %咔咔%"),
    ;

    static class Const {
        public static final char POINT = '\'';
        public static final char eq = '=';
        public static final char gt = '>';
        public static final char lt = '<';
        public static final String like = "like";
    }

    private static String whereSqlValueList(String key, Object val, String inOpt) {
        if (val instanceof Collection) {
            ((Collection<?>) val).stream().filter(Objects::nonNull).map(v -> POINT + v.toString() + POINT).collect(Collectors.joining(","));
        }
        return key + " " + inOpt + " (" + val + ")";
    }

    BiFunction<String, Object, String> sqlFunction;
    String demo;
}