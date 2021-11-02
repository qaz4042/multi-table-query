package com.lzb.mpmt.service.multiwrapper.sqlsegment.jdbc;

import com.lzb.mpmt.service.MultiWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MysqlExecutor {

    private static JdbcTemplate jdbcTemplate;

    @Autowired    // 自动注入，spring boot会帮我们实例化一个对象
    public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        MysqlExecutor.jdbcTemplate = jdbcTemplate;
    }

    public static <MAIN> List<MAIN> query(MultiWrapper wrapper) {
        String sql = wrapper.computeSql();
        Statement stm = null;
        ResultSet rs = null;
        Connection con = null;
        List<MAIN> MAINs = jdbcTemplate.query(sql, new RowMapper<MAIN>() {
            @Override
            public MAIN mapRow(ResultSet resultSet, int i) throws SQLException {
                MAIN MAIN = (MAIN) wrapper.getWrapperMain().getTableName().getClass().getNestHost();// todo  改成 class
                MAIN.setId(resultSet.getInt("id"));
                MAIN.setName(resultSet.getString("name"));
                MAIN.setPassword(resultSet.getString("password"));
                MAIN.setSex(resultSet.getString("sex"));
                MAIN.setAge(resultSet.getInt("age"));
                return MAIN;
            }
        });

    }
}
