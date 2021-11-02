package com.lzb.mpmt.service.multiwrapper.sqlsegment;

import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.lzb.mpmt.service.multiwrapper.util.MutilUtil;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.SerializedLambda;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Administrator
 */
@SuppressWarnings({"unused", "unchecked", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface MultiWrapperSelect<T, Wrapper extends MultiWrapperSelect<T, Wrapper>> {
    String getTableName();
    void setTableName(String tableName);

    List<String> getSelectProps();
    void setSelectProps(List<String> list);

    /***
     * 设置查询字段列表,不设置则默认*(全查询)
     * @param propFuncs 多个字段
     * @param <VAL> 字段泛型
     * @return 当前wrapper
     */
    default <VAL> Wrapper select(MultiFunction<T, VAL>... propFuncs) {
        if (null != propFuncs && propFuncs.length > 0) {
            if (null == getTableName()) {
                this.setTableName(SerializedLambda.resolveCache(propFuncs[0]).getClazzNameUnderline());
            }
            this.setSelectProps(Arrays.stream(propFuncs).map(propFunc -> SerializedLambda.resolveCache(propFunc).getPropNameUnderline()).collect(Collectors.toList()));
        }
        return (Wrapper) this;
    }

    /**
     * 拼接出select字段列表
     * @return sqlSelectProps
     */
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
