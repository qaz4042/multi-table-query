package com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Administrator
 */
@Slf4j
public class MultiJdbcTemplateSqlExecutor implements IMultiSqlExecutor {

    private final JdbcTemplate jdbcTemplate;

    public MultiJdbcTemplateSqlExecutor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> List<T> executeSql(String sql, Function<ResultSet, T> function) {
        return jdbcTemplate.query(sql, (rs, i) -> function.apply(rs));
    }

    @Override
    public Map<String, Object> executeSql(String sql) {
        return jdbcTemplate.queryForMap(sql);
    }
}
