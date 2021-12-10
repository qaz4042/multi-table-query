package com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * JDBC 基础查询实现 (各方面性能可能需要单独优化(连接池,业务代码易用性等),推荐使用 {@link MultiJdbcJdbcSpringSqlExecutor})
 *
 * @author Administrator
 */
@Slf4j
@Deprecated
public class MultiJdbcJdbcSqlExecutor implements MultiSqlExecutorIntf {

    public static String driver = "com.mysql.jdbc.Driver";

    public static String url = "jdbc:mysql://42.123.87.49:3306/aaaaa?useUnicode=true&characterEncoding=utf-8";

    public static String user = "***";

    public static String password = "****";

    @Override
    public <T> List<T> select(String sql, Function<ResultSet, T> function) {
        log.info("Multi 查询Sql:{}", sql);

        List<T> list = new ArrayList<>(2000);
        Connection conn = null;
        Statement stmt = null;
        try {
            log.info("STEP 2: Register JDBC driver");
            Class<?> driverClass = Class.forName(driver);
            Driver driver = (Driver) driverClass.newInstance();
            DriverManager.registerDriver(driver);
            log.info("STEP 3:Open a connection");
            log.info("Connecting to database...");
            conn = DriverManager.getConnection(url, user, password);

            log.info("STEP 4: Execute a query");
            log.info("Creating statement...");
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            log.info("STEP 5: Extract data from result set");
            while (rs.next()) {
                list.add(function.apply(rs));
            }
            log.info("STEP 6: Clean-up environment");
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            log.info("Handle errors for JDBC");
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
                log.error("sql:" + sql + "\n", se2);
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        return list;
    }
}
