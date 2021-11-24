package com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface IMultiSqlExecutor {

    /**
     * 执行sql返回结果集
     *
     * @param sql sql
     * @return 结果集(Set.next ()一直遍历下去)
     */
    <T> List<T> executeSql(String sql, Function<ResultSet, T> function);

    /**
     * 执行sql返回结果集
     *
     * @param sql sql
     * @return 结果集(Set.next ()一直遍历下去)
     */
    Map<String, Object> executeSql(String sql);
}
