package com.lzb.mpmt.service.multiwrapper.dto;

import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;
import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class MultiAggregateResultMap {
    private MultiConstant.MultiAggregateTypeEnum aggregateType;
    private String relationCode;
    private String propName;

    public MultiAggregateResultMap(String append) {
        String[] split = append.split("\\.");
        aggregateType = MultiConstant.MultiAggregateTypeEnum.valueOf(split[0]);
        relationCode = split[1];
        propName = split[2];
    }
}