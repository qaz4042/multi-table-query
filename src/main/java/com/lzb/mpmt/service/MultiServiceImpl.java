package com.lzb.mpmt.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.List;

public interface MultiServiceImpl<MAIN> {
    default List<MAIN> list(Wrapper<MAIN> wrapperMain, Wrapper<?>... wrapperOther) {
        wrapperMain.getSqlSegment()
        Wrappers.lambdaQuery()
        return
    }
}
