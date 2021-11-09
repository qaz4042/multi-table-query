package com.lzb.mpmt.service.multiwrapper.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author Administrator
 */
@Getter
@AllArgsConstructor
public enum WhereOptEnum {
    /***/
    eq("%s = '%s'", "age = 18"),
    isNull("%s is null", "age is null"),
    isNotNull("%s is not null", "age is not null"),
    in("%s in (%s)", "age in (18,19)"),
    not_in("%s not in (%s)", "age not in (18,19)"),
    gt("%s > '%s'", "age > '18'"),
    ge("%s >= '%s'", "age >= '18'"),
    lt("%s < '%s'", "age < '18'"),
    le("%s <= '%s'", "age <= '18'"),
    likeDefault("%s like '%%%s%%'", "name like '%咔咔%'"), //%%转义为%
    ;

    private final String template;
    private final String demo;
}