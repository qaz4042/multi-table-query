package com.lzb.mpmt.service.intf;

import com.lzb.mpmt.service.common.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings({"unused", "unchecked"})
public interface MultiWrapperSelect<T, Wrapper extends MultiWrapperSelect<T, Wrapper>> {
    String getTableName();

    void setTableName(String tableName);

    List<String> getSelectProps();

    void setSelectProps(List<String> list);

    //    @SafeVarargs
    default <VAL> Wrapper select(MultiFunction<T, VAL>... propFuncs) {
        if (null != propFuncs && propFuncs.length > 0) {
            if (null == getTableName()) {
                setTableName(SerializedLambda.resolveCache(propFuncs[0]).getClazzNameUnderline());
            }
            setSelectProps(Arrays.stream(propFuncs).map(propFunc -> SerializedLambda.resolveCache(propFunc).getPropNameUnderline()).collect(Collectors.toList()));
        }
        return (Wrapper) this;
    }

    default String getSelectSql() {
        return (null == getSelectProps() || getSelectProps().size() <= 0) ? "" : ("select " + String.join(",", getSelectProps()));
    }
}
