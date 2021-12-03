package com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata;

import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;

/**
 * @author Administrator
 */
public interface IWhereData {
    /**
     * 获取where内的属性信息
     *
     * @param className 表名
     * @return sqlWhereProps
     */
    default String getSqlWhereProps(String className) {
        return MultiConstant.Strings.EMPTY;
    }
}
