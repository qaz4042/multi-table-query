package com.bebetter.mtq.service.multiwrapper.dto;

import java.util.List;

/**
 * 分页信息对象
 *
 * @author Administrator
 */
public interface IMultiPage<T> {
    /* ------------- 入参 ------------- */
    /**
     * 每页大小
     * @return 每页大小
     */
    Long getPageSize();
    /**
     * 当前第几页
     * @return 当前第几页
     */
    Long getCurrPage();

    /* ------------- 返回 ------------- */
    /**
     * 分页列表内容 <必有>
     * @return 分页列表内容
     */
    List<T> getRecords();
    /**
     * 总条数 <必有>
     * @return 总条数
     */
    Long getTotal();

    /**
     * 统计信息
     * 例如 {'user__userWallet.enableBalance':"1000"}
     *
     * @return 统计信息
     */
    MultiAggregateResult getAggregateResult();
    /**
     * 附加信息
     * @return 附加信息
     */
    Object getAttach();

    void setPageSize(Long pageSize);
    void setCurrPage(Long currPage) ;
    void setRecords(List<T> records);
    void setTotal(Long total) ;
    void setAggregateResult(MultiAggregateResult aggregateResult) ;
    void setAttach(Object attach);
}
