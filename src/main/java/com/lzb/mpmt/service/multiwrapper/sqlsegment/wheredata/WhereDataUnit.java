package com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata;

import com.lzb.mpmt.service.multiwrapper.enums.IMultiEnum;
import com.lzb.mpmt.service.multiwrapper.enums.WhereOptEnum;
import com.lzb.mpmt.service.multiwrapper.util.MultiConstant;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.ClientPreparedQueryBindings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

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
        return String.format(opt.getTemplate(), tableName + "." + propName + " ", formatValues(values));
    }

    /***
     * 格式化value
     */
    private static Object formatValues(Object value) {
        if (null == value) {
            return null;
        } else if (value instanceof IMultiEnum) {
            value = ((IMultiEnum<?>) value).getValue();
        } else if (value instanceof Enum) {
            value = ((Enum<?>) value).name();
        } else if (value instanceof Date) {
            value = MultiConstant.DateTimes.DATE_FORMAT.format(value);
        } else if (value instanceof LocalDateTime) {
            value = MultiConstant.DateTimes.DATE_TIME_FORMAT.format((LocalDateTime) value);
        } else if (value instanceof LocalTime) {
            value = MultiConstant.DateTimes.TIME_FORMAT.format((LocalTime) value);
        } else if (value instanceof String) {
            //防止SQL注入
            value = ClientPreparedQueryBindings.sqlAvoidAttack((String) value);
        } else if (value instanceof Collection) {
            if (((Collection<?>) value).size() == 0) {
                return "1!=1";
            }
            value = ((Collection<?>) value).stream().filter(Objects::nonNull).map(v -> '\'' + v.toString() + '\'').collect(Collectors.joining(","));
        } else if (value.getClass().isArray()) {
            if (((Object[]) value).length == 0) {
                return "1!=1";
            }
            value = Arrays.stream(((Object[]) value)).filter(Objects::nonNull).map(v -> '\'' + v.toString() + '\'').collect(Collectors.joining(","));
        }
        return value;
    }
}
