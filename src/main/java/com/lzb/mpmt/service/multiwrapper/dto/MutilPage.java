package com.lzb.mpmt.service.multiwrapper.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页信息对象
 *
 * @author Administrator
 */
@Data
public class MutilPage<T> {
    private Long pageSize;
    private Long currPage;
    private Long total;
    private String orders;
    private List<T> reocrds;
    private Object attach;
    private T sum;
}
