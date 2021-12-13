package com.bebetter.mtq.service.multiwrapper.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * mysql聚合函数返回信息
 * 统一只有三种类型(根据源字段类型)
 * Long,BigDecimal,String
 * map的key为字段名
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MultiAggregateResult {
    /**
     * 求和(支持多字段计算)
     */
    private MultiHashMap<String, ?> sum = MultiHashMap.emptyMap();
    /**
     * 求平均值
     */
    private MultiHashMap<String, ?> avg = MultiHashMap.emptyMap();
    /**
     * 计数
     */
    private Long count;
    /**
     * 计数去重
     */
    private MultiHashMap<String, Long> countDistinct = MultiHashMap.emptyMap();
    /**
     * 最大值
     */
    private MultiHashMap<String, ?> max = MultiHashMap.emptyMap();
    /**
     * 最小值
     */
    private MultiHashMap<String, ?> min = MultiHashMap.emptyMap();
    /**
     * 分组组合拼接
     */
    private MultiHashMap<String, String> groupConcat = MultiHashMap.emptyMap();
}
