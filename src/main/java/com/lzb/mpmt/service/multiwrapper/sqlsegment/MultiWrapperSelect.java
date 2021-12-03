package com.lzb.mpmt.service.multiwrapper.sqlsegment;

import com.lzb.mpmt.service.multiwrapper.util.MultiRelationCaches;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.SerializedLambda;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Administrator
 */
@SuppressWarnings({"unused", "unchecked", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface MultiWrapperSelect<T, Wrapper extends MultiWrapperSelect<T, Wrapper>> {
    String getClassName();

    void setClassName(String className);

    List<String> getSelectFields();

    void setSelectFields(List<String> list);

    Class<T> getClazz();

    /***
     * 设置查询字段列表,不设置则默认*(全查询)
     *
     * @param propFuncs 多个字段
     * @param <VAL> 字段泛型
     * @return 当前wrapper
     */
    default <VAL> Wrapper select(MultiFunction<T, VAL>... propFuncs) {
        if (!MultiUtil.isEmpty(propFuncs)) {
            if (null == getClassName()) {
                this.setClassName(MultiUtil.firstToLowerCase(SerializedLambda.resolveCache(propFuncs[0]).getClazz().getSimpleName()));
            }
            this.setSelectFields(Arrays.stream(propFuncs).map(propFunc -> SerializedLambda.resolveCache(propFunc).getPropName()).collect(Collectors.toList()));
        }
        return (Wrapper) this;
    }

    /**
     * 拼接出select字段列表
     *
     * @param relationCode 当前关系的code
     * @return sqlSelectProps
     */
    default String getSqlSelectProps(String relationCode) {
        List<String> selectFieldNames = getSelectFields();
        //默认用全字段去查询,不用*,方便后续多表字段对应
        if (null == selectFieldNames) {
            selectFieldNames = MultiRelationCaches.getFieldNamesByClass(getClazz());
            setSelectFields(selectFieldNames);
        }
        return selectFieldNames.stream().map(fieldName -> "  " + relationCode + "." + fieldName + " as " + "`" + relationCode + "." + fieldName + "`")
                .collect(Collectors.joining(",\n"));
    }

    /**
     * 获取select的全字段
     *
     * @return sqlSelectProps
     */
    default List<String> getSelectFieldNames() {
        List<String> selectFieldNames = getSelectFields();
        //默认用全字段去查询,不用*,方便后续多表字段对应
        if (null == selectFieldNames) {
            selectFieldNames = MultiRelationCaches.getFieldNamesByClass(getClazz());
            setSelectFields(selectFieldNames);
        }
        return selectFieldNames;
    }
}
