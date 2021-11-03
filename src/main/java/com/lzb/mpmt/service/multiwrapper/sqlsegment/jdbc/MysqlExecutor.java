package com.lzb.mpmt.service.multiwrapper.sqlsegment.jdbc;

import com.lzb.mpmt.service.MultiWrapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class MysqlExecutor {

    private static JdbcTemplate jdbcTemplate;

    @Autowired    // 自动注入，spring boot会帮我们实例化一个对象
    public static void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        MysqlExecutor.jdbcTemplate = jdbcTemplate;
    }

    @SneakyThrows
    public static <MAIN> List<MAIN> query(MultiWrapper<MAIN> wrapper) {
        String sql = wrapper.computeSql();

        Class<MAIN> mainClazz = wrapper.getWrapperMain().getClazz();

        return jdbcTemplate.query(sql, (resultSet, i) -> buildReturn(mainClazz, resultSet));
    }

    @SneakyThrows
    private static <MAIN> MAIN buildReturn(Class<MAIN> mainClazz, ResultSet resultSet) {
        MAIN MAIN = mainClazz.newInstance();
        //

//        MAIN.setId(resultSet.getInt("id"));
//        MAIN.setName(resultSet.getString("name"));
//        MAIN.setPassword(resultSet.getString("password"));
//        MAIN.setSex(resultSet.getString("sex"));
//        MAIN.setAge(resultSet.getInt("age"));
        return MAIN;
    }
}
