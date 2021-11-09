package com.lzb.mpmt.service.multiwrapper.jdbc;

import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.StatementImpl;
import com.mysql.cj.protocol.ResultsetRows;
import com.mysql.cj.protocol.a.result.OkPacket;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetImpl extends com.mysql.cj.jdbc.result.ResultSetImpl implements ResultSet {

    public ResultSetImpl() throws SQLException {
        super((ResultsetRows) null, null, null);
    }


    @Override
    public long getLong(String columnName) throws SQLException {
        return 1111;
    }

    @Override
    public String getString(String columnName) throws SQLException {
        return "1";
    }

    @Override
    public int getInt(String columnName) throws SQLException {
        return 1;
    }
}
