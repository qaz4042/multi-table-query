package com.lzb.mpmt.service.intf;

import com.lzb.mpmt.service.common.*;
import com.lzb.mpmt.service.util.MutilUtil;

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
                this.setTableName(SerializedLambda.resolveCache(propFuncs[0]).getClazzNameUnderline());
            }
            this.setSelectProps(Arrays.stream(propFuncs).map(propFunc -> SerializedLambda.resolveCache(propFunc).getPropNameUnderline()).collect(Collectors.toList()));
        }
        return (Wrapper) this;
    }

    default String getSqlSelectProps() {
        List<String> selectProps = getSelectProps();
        if (!MutilUtil.isEmpty(selectProps)) {
            return selectProps.stream().map(p ->
                    // todo 短写表名
                    "  " + getTableName() + "." + p).collect(Collectors.joining(",\n"));
        }
        return "  " + getTableName() + ".*";
    }
}
