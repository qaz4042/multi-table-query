package com.lzb.mpmt.service.multiwrapper.jdbc;

import com.lzb.mpmt.service.MultiWrapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
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
        String mainTableName = wrapper.getWrapperMain().getTableName();
        String mainIdPropName = mainTableName + ".id";

        return jdbcTemplate.query(sql, (resultSet, i) -> buildReturn(mainClazz,mainIdPropName, resultSet));
    }

    @SneakyThrows
    private static <MAIN> MAIN buildReturn(Class<MAIN> mainClazz,String mainIdPropName, ResultSet resultSet) {
        resultSet.next()
        MAIN MAIN = mainClazz.newInstance();
        resultSet.getString(mainIdPropName)
        //

//        MAIN.setId(resultSet.getInt("id"));
//        MAIN.setName(resultSet.getString("name"));
//        MAIN.setPassword(resultSet.getString("password"));
//        MAIN.setSex(resultSet.getString("sex"));
//        MAIN.setAge(resultSet.getInt("age"));
        return MAIN;
    }
}
