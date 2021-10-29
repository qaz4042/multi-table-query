package com.lzb.mpmt.service.common;

public interface IWhereTreeData {
    default String toSql(String tableName) {
        return "";
    }
}
