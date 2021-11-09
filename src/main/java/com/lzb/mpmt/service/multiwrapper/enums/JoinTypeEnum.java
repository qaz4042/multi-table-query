package com.lzb.mpmt.service.multiwrapper.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Administrator
 */

@Getter
@AllArgsConstructor
public enum JoinTypeEnum {
    /***/
    left_join("left join "),
    inner_join("inner join "),
    right_join("right join "),
    ;
    private final String sql;
}
