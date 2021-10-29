package com.lzb.mpmt.service.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum WhereOptEnum {
    eq((key, val) -> key + " = " + val, "age = 18"),
    in((key, val) ->  whereSqlIn(key, val), "in", "age in (18,19)"),
    not_in((key, val) -> key + " not in (" + val + ")", "age not in (18,19)"),
    gt((key, val) -> key + " > " + val, "age > 18"),
    ge((key, val) -> key + " >= " + val, "age >= 18"),
    lt((key, val) -> key + " < " + val, "age < 18"),
    le((key, val) -> key + " <= " + val, "age <= 18"),
    like((key, val) -> key + " like " + val, "name like %咔咔%"),
    ;

    private static String whereSqlIn(Object key, Object val, String inOpt) {
        if (val instanceof Collection) {
            ((Collection<?>) val).stream().filter(Objects::nonNull).map(v -> "\"" + v + "\"").collect(Collectors.joining(","));
        }
        return key + " " + inOpt + " (" + val + ")";
    }

    BiFunction<Object, Object, String> sqlFunction;
    String demo;
}