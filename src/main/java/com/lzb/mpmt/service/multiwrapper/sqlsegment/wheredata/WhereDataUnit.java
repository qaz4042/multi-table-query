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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.lzb.mpmt.service.multiwrapper.enums.WhereOptEnum.Const.POINT;

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
        return opt.getSqlFunction().apply(tableName + "." + propName + " ", formatValues(values));
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
        } else if (value instanceof Date) {
            value = DATE_FORMAT.format(value);
        } else if (value instanceof LocalDateTime) {
            value = DATE_TIME_FORMAT.format((LocalDateTime) value);
        } else if (value instanceof LocalTime) {
            value = TIME_FORMAT.format((LocalTime) value);
        } else if (value instanceof String) {
            //防止SQL注入
            value = ClientPreparedQueryBindings.sqlAvoidAttack((String) value);
        } else if (value instanceof Collection) {
            value = ((Collection<?>) value).stream().filter(Objects::nonNull).map(v -> POINT + v.toString() + POINT).collect(Collectors.joining(","));
            if (((Collection<?>) value).size() == 0) {
                return "1!=1";
            }
            value = " (" + value + ")";
        } else if (value.getClass().isArray()) {
            value = Arrays.stream(((Object[]) value)).filter(Objects::nonNull).map(v -> POINT + v.toString() + POINT).collect(Collectors.joining(","));
            if (((Object[]) value).length == 0) {
                return "1!=1";
            }
            value = " (" + value + ")";
        }
        return value;
    }
}
