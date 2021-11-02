package com.lzb.mpmt.service.multiwrapper.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 类和类的关系  可以自动生成left join 统一规范sql
 */
@AllArgsConstructor
@Getter
public enum ClassRelationOneOrManyEnum {
    /***/
    ONE("在关系里为一对多/一对一里面的一"),
    MANY("在关系里为一对多/多对多里面的多"),
    ;
    private String label;

}
