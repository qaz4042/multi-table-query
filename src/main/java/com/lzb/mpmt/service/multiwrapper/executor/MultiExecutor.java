package com.lzb.mpmt.service.multiwrapper.executor;

import com.lzb.mpmt.service.multiwrapper.dto.IMultiPage;
import com.lzb.mpmt.service.multiwrapper.dto.MultiAggregateResult;
import com.lzb.mpmt.service.multiwrapper.wrapper.MultiWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Administrator
 */
@Slf4j
@Service
public class MultiExecutor {

    @SneakyThrows
    public static <MAIN> List<MAIN> list(MultiWrapper<MAIN> wrapper) {
        return MultiExecutorInner.list(wrapper.getWrapperInner());
    }

    @SneakyThrows
    public static <MAIN> MAIN getOne(MultiWrapper<MAIN> wrapper) {
        return MultiExecutorInner.getOne(wrapper.getWrapperInner());
    }

    /**
     * 执行分页查询
     *
     * @param page    分页信息
     * @param wrapper 表信息,聚合信息,过滤条件...
     * @return 聚合查询结果 例如 {"sum":{"userAndUserStaff.balance":"100.00"}}
     */
    @SneakyThrows
    public static <MAIN> IMultiPage<MAIN> page(IMultiPage<MAIN> page, MultiWrapper<MAIN> wrapper) {
        return MultiExecutorInner.page(page, wrapper.getWrapperInner());
    }

    /**
     * 执行聚合查询
     *
     * @param wrapper 表信息,聚合信息,过滤条件...
     * @return 聚合查询结果 例如 {"sum":{"userAndUserStaff.balance":"100.00"}}
     */
    @SneakyThrows
    public static <MAIN> MultiAggregateResult aggregate(MultiWrapper<MAIN> wrapper) {
        return MultiExecutorInner.aggregate(wrapper.getWrapperInner());
    }
}
