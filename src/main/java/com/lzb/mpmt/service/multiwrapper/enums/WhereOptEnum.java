package com.lzb.mpmt.service.multiwrapper.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.lzb.mpmt.service.multiwrapper.enums.WhereOptEnum.Const.POINT;

/**
 * @author Administrator
 */

@SuppressWarnings("ConstantConditions")
@Getter
@AllArgsConstructor
public enum WhereOptEnum {
    /***/
    eq((key, val) -> "{0}='{1}'", "age = 18"),
    isNull((key, val) -> "{0} is null", "age is null"),
    isNotNull((key, val) -> "{0} is not null", "age is not null"),
    in((key, val) -> "{0} in {1}", "age in (18,19)"),
    not_in((key, val) -> "{0} not in {1}", "age not in (18,19)"),
    gt((key, val) -> "{0} > '{1}'", "age > '18'"),
    ge((key, val) -> "{0} >= '{1}'", "age >= '18'"),
    lt((key, val) -> "{0} < '{1}'","age < '18'"),
    le((key, val) -> "{0} <= '{1}'", "age <= '18'"),
    likeDefault((key, val) -> "{0} like '%{1}%'", "name like '%咔咔%'"),
    ;

    private final BiFunction<String, Object, String> sqlFunction;
    private final String demo;
}