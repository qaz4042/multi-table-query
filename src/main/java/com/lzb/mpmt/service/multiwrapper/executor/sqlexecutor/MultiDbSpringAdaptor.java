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
public class MultiDbSpringAdaptor implements MultiDbAdaptor {

    private final JdbcTemplate jdbcTemplate;

    public MultiDbSpringAdaptor(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> List<T> select(String sql, Function<ResultSet, T> function) {
        log.info("Multi 查询Sql:{}", sql);
        return jdbcTemplate.query(sql, (rs, i) -> function.apply(rs));
    }

    @Override
    public Map<String, Object> selectFirstRow(String sql) {
        log.info("Multi 查询Sql:{}", sql);
        return jdbcTemplate.queryForMap(sql);
    }
}
