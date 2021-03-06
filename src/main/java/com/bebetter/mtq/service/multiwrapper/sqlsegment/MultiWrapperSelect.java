package com.bebetter.mtq.service.multiwrapper.sqlsegment;

import com.bebetter.mtq.service.multiwrapper.util.MultiRelationCaches;
import com.bebetter.mtq.service.multiwrapper.util.MultiTuple2;
import com.bebetter.mtq.service.multiwrapper.util.MultiUtil;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.SerializedLambdaData;

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
        MultiTuple2<String, List<String>> result = MultiUtil.calcMultiFunctions(SerializedLambdaData::getPropName, propFuncs);
        if (null == getClassName()) {
            setClassName(result.getT1());
        }
        this.setSelectFields(result.getT2());
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
        return selectFieldNames.stream().map(fieldName -> "  " + relationCode + "." + MultiUtil.camelToUnderline(fieldName) + " as " + "`" + relationCode + "." + fieldName + "`")
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
