package com.lzb.mpmt.service.common;

import com.lzb.mpmt.service.util.MutilUtil;

public interface IWhereTreeData {
    default String getSqlWhereProps(String tableName) {
        return MutilUtil.EMPTY;
    }
}
