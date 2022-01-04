package com.bebetter.mtq.service.multiwrapper;

import com.bebetter.mtq.service.multiwrapper.dto.IMultiPage;
import com.bebetter.mtq.service.multiwrapper.dto.MultiAggregateResult;
import com.bebetter.mtq.service.multiwrapper.executor.MultiExecutorInner;
import com.bebetter.mtq.service.multiwrapper.wrapper.MultiWrapper;
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
    public static <MAIN, DTO> List<DTO> list(MultiWrapper<MAIN, DTO> wrapper) {
        return MultiExecutorInner.list(wrapper.getWrapperInner());
    }

    @SneakyThrows
    public static <MAIN, DTO> DTO getOne(MultiWrapper<MAIN, DTO> wrapper) {
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
    public static <MAIN, DTO> IMultiPage<DTO> page(IMultiPage<DTO> page, MultiWrapper<MAIN, DTO> wrapper) {
        return MultiExecutorInner.page(page, wrapper.getWrapperInner());
    }

    /**
     * 执行聚合查询
     *
     * @param wrapper 表信息,聚合信息,过滤条件...
     * @return 聚合查询结果 例如 {"sum":{"userAndUserStaff.balance":"100.00"}}
     */
    @SneakyThrows
    public static <MAIN, DTO> MultiAggregateResult aggregate(MultiWrapper<MAIN, DTO> wrapper) {
        return MultiExecutorInner.aggregate(wrapper.getWrapperInner());
    }
}
