package com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata;

import com.lzb.mpmt.service.multiwrapper.util.MutilUtil;

/**
 * @author Administrator
 */
public interface IWhereData {
    /**
     * 获取where内的属性信息
     *
     * @param tableName 表名
     * @return sqlWhereProps
     */
    default String getSqlWhereProps(String tableName) {
        return MutilUtil.EMPTY;
    }
}
