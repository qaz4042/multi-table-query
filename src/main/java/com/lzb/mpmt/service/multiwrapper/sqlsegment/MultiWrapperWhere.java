package com.lzb.mpmt.service.multiwrapper.sqlsegment;

import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.SerializedLambda;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.SerializedLambdaData;
import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant.WhereAndOrEnum;
import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant.WhereOptEnum;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata.WhereDataTree;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata.WhereDataUnit;
import lombok.SneakyThrows;

import java.util.function.Consumer;

/**
 * @author Administrator
 */
@SuppressWarnings({"unused", "unchecked", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface MultiWrapperWhere<T, Wrapper extends MultiWrapperWhere<T, Wrapper>> {

    String getTableName();

    void setTableName(String tableName);

    WhereDataTree getWhereTree();

    @SneakyThrows
    default <VAL> Wrapper and(Consumer<Wrapper> andContent) {
        // noinspection deprecation
        Wrapper wrapper = (Wrapper) this.getClass().newInstance();
        andContent.accept(wrapper);
        getWhereTree().getWhereDatas().add(wrapper.getWhereTree());
        return (Wrapper) this;
    }

    /***
     * 一个层级上只要有一个or条件,认定本层所有条件都是or关系
     */
    default <VAL> Wrapper or() {
        getWhereTree().setAndOr(WhereAndOrEnum.or);
        return (Wrapper) this;
    }

    default <VAL> Wrapper eq(MultiFunction<T, VAL> prop, VAL value) {
        return eq(true, prop, value);
    }

    default <VAL> Wrapper isNull(MultiFunction<T, VAL> prop) {
        return isNull(true, prop);
    }

    default <VAL> Wrapper isNotNull(MultiFunction<T, VAL> prop) {
        return isNotNull(true, prop);
    }


    default <VAL> Wrapper gt(MultiFunction<T, VAL> prop, VAL value) {
        return gt(true, prop, value);
    }

    default <VAL> Wrapper ge(MultiFunction<T, VAL> prop, VAL value) {
        return ge(true, prop, value);
    }

    default <VAL> Wrapper lt(MultiFunction<T, VAL> prop, VAL value) {
        return lt(true, prop, value);
    }

    default <VAL> Wrapper le(MultiFunction<T, VAL> prop, VAL value) {
        return le(true, prop, value);
    }

    default <VAL> Wrapper in(MultiFunction<T, VAL> prop, VAL... values) {
        return in(true, prop, values);
    }

    default <VAL> Wrapper notIn(MultiFunction<T, VAL> prop, VAL... values) {
        return notIn(true, prop, values);
    }

    default <VAL> Wrapper likeDefault(MultiFunction<T, VAL> prop, VAL value) {
        return likeDefault(true, prop, value);
    }


    default <VAL> Wrapper eq(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, WhereOptEnum.eq);
        return (Wrapper) this;
    }

    default <VAL> Wrapper isNull(Boolean condition, MultiFunction<T, VAL> prop) {
        this.addWhereTreeData(condition, prop, null, WhereOptEnum.isNull);
        return (Wrapper) this;
    }

    default <VAL> Wrapper isNotNull(Boolean condition, MultiFunction<T, VAL> prop) {
        this.addWhereTreeData(condition, prop, null, WhereOptEnum.isNotNull);
        return (Wrapper) this;
    }

    default <VAL> Wrapper gt(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, WhereOptEnum.gt);
        return (Wrapper) this;
    }

    default <VAL> Wrapper ge(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, WhereOptEnum.ge);
        return (Wrapper) this;
    }

    default <VAL> Wrapper lt(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, WhereOptEnum.lt);
        return (Wrapper) this;
    }

    default <VAL> Wrapper le(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, WhereOptEnum.le);
        return (Wrapper) this;
    }

    default <VAL> Wrapper in(Boolean condition, MultiFunction<T, VAL> prop, VAL... values) {
        this.addWhereTreeData(condition, prop, values, WhereOptEnum.in);
        return (Wrapper) this;
    }

    default <VAL> Wrapper notIn(Boolean condition, MultiFunction<T, VAL> prop, VAL... values) {
        this.addWhereTreeData(condition, prop, values, WhereOptEnum.not_in);
        return (Wrapper) this;
    }

    default <VAL> Wrapper likeDefault(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, WhereOptEnum.likeDefault);
        return (Wrapper) this;
    }

    /**
     * 递归 getSqlWhereProps 拼接最后结果
     *
     * @return where条件中的字段条件信息(拼接后)
     */
    default String getSqlWhereProps() {
        return getWhereTree().getSqlWhereProps(getTableName());
    }

    /**
     * 添加过滤条件
     *
     * @param condition 方便开发,false忽略此次添加
     * @param prop      字段名
     * @param values    字段值
     * @param opt       等于/大于 等条件判断
     * @param <VAL>     字段的泛型
     */
    private <VAL> void addWhereTreeData(Boolean condition, MultiFunction<T, VAL> prop, Object values, WhereOptEnum opt) {
        if (!condition) {
            return;
        }
        SerializedLambdaData resolve = SerializedLambda.resolveCache(prop);
        if (null == getTableName()) {
            setTableName(resolve.getTableName());
        }
        String propNameUnderline = resolve.getFieldName();
        getWhereTree().getWhereDatas().add(new WhereDataUnit(propNameUnderline, opt, values));
    }
}
