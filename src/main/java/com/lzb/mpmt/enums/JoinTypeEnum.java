package com.lzb.mpmt.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

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
