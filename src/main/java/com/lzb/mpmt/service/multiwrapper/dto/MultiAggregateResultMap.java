package com.lzb.mpmt.service.multiwrapper.dto;

import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;
import lombok.Data;

@Data
public class MultiAggregateResultMap {
    private MultiConstant.MultiAggregateTypeEnum aggregateType;
    private String relationCode;
    private String fieldName;

    public MultiAggregateResultMap(String append) {
        String[] split = append.split(".");
        aggregateType = MultiConstant.MultiAggregateTypeEnum.valueOf(split[0]);
        relationCode = split[1];
        fieldName = split[2];
    }
}