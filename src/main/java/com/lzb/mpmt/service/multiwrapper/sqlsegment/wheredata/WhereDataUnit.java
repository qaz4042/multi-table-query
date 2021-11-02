package com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata;

import com.lzb.mpmt.service.multiwrapper.enums.WhereOptEnum;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.ClientPreparedQueryBindings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WhereDataUnit implements IWhereData {
    private String propName;
    private WhereOptEnum opt;
    private Object values;


    @Override
    public String getSqlWhereProps(String tableName) {
        return opt.getSqlFunction().apply(tableName + "." + propName, formatValues(values));
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
//    private static final String CHECKSQL = "^(.+)\\\\sand\\\\s(.+)|(.+)\\\\sor(.+)\\\\s$";//防止SQL注入

    /***
     * 格式化value
     */
    private static Object formatValues(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Date) {
            value = DATE_FORMAT.format(value);
        }
        if (value instanceof LocalDateTime) {
            value = DATE_TIME_FORMAT.format((LocalDateTime) value);
        }
        if (value instanceof LocalTime) {
            value = TIME_FORMAT.format((LocalTime) value);
        }
        if (value instanceof String) {
            //防止SQL注入
            value = ClientPreparedQueryBindings.sqlAvoidAttack((String) value);
        }
        return value;
    }

}
