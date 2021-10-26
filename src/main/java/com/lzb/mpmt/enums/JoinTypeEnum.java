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
    left_join,
    inner_join,
    right_join,
    ;
}
