package com.lzb.mpmt.service.multiwrapper.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 分页信息对象
 *
 * @author Administrator
 */
@Data
public class MultiPage<T> {
    private Long pageSize;
    private Long currPage;
    private Long total;
    private String orders;
    private List<T> records;
    private Object attach;


    /**
     * 统一数字类型的字段的求和结果
     * key: relationCode.filedName 转驼峰
     * value: 求和结果
     */
    private Map<String, Object> sumMap;
}
