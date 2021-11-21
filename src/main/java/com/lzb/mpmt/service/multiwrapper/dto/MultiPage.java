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
public class MultiPage<T> implements IMultiPage<T> {

    /* ------------- 入参 ------------- */
    /**
     * 每页大小
     */
    private Long pageSize;
    /**
     * 当前第几页
     */
    private Long currPage;
    /**
     * 排序信息
     * 例如 username asc,id desc
     */
    private String orders;


    /* ------------- 返回 ------------- */
    /**
     * 分页列表内容 <必有>
     */
    private List<T> records;
    /**
     * 总条数 <必有>
     */
    private Long total;
    /**
     * 统计信息
     * 例如
     * {    "sum":{"user__userWallet.enableBalance":"1000"}   }
     */
    private MultiAggregateResult aggregateResult;
    /**
     * 附加信息
     */
    private Object attach;
}
