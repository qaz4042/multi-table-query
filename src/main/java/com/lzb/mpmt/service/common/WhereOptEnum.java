package com.lzb.mpmt.service.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WhereOptEnum {
    eq("age = 18"),
    in("age in (18,19)"),
    not_in("age not in (18,19)"),
    gt("age > 18"),
    ge("age >= 18"),
    lt("age < 18"),
    le("age <= 18"),
    like("name like %咔咔%"),
    ;
    String demo;
}