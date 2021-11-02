package com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata;

import com.lzb.mpmt.service.multiwrapper.util.MutilUtil;

public interface IWhereTreeData {
    default String getSqlWhereProps(String tableName) {
        return MutilUtil.EMPTY;
    }
}
