package com.bebetter.mtq.service.multiwrapper.sqlsegment;

import com.bebetter.mtq.service.multiwrapper.constant.MultiConstant;
import com.bebetter.mtq.service.multiwrapper.sqlsegment.wheredata.WhereDataTree;
import com.bebetter.mtq.service.multiwrapper.sqlsegment.wheredata.WhereDataUnit;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.bebetter.mtq.service.multiwrapper.util.MultiUtil;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.SerializedLambda;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.SerializedLambdaData;
import lombok.SneakyThrows;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@SuppressWarnings({"unused", "unchecked", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface MultiWrapperWhere<T, Wrapper extends MultiWrapperWhere<T, Wrapper>> {

    String getClassName();

    void setClassName(String className);

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
        getWhereTree().setAndOr(MultiConstant.WhereAndOrEnum.or);
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

    default <VAL> Wrapper likeAll(MultiFunction<T, VAL> prop, VAL value) {
        return likeAll(true, prop, value);
    }


    default <VAL> Wrapper eq(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, MultiConstant.WhereOptEnum.eq);
        return (Wrapper) this;
    }

    default <VAL> Wrapper isNull(Boolean condition, MultiFunction<T, VAL> prop) {
        this.addWhereTreeData(condition, prop, null, MultiConstant.WhereOptEnum.isNull);
        return (Wrapper) this;
    }

    default <VAL> Wrapper isNotNull(Boolean condition, MultiFunction<T, VAL> prop) {
        this.addWhereTreeData(condition, prop, null, MultiConstant.WhereOptEnum.isNotNull);
        return (Wrapper) this;
    }

    default <VAL> Wrapper gt(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, MultiConstant.WhereOptEnum.gt);
        return (Wrapper) this;
    }

    default <VAL> Wrapper ge(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, MultiConstant.WhereOptEnum.ge);
        return (Wrapper) this;
    }

    default <VAL> Wrapper lt(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, MultiConstant.WhereOptEnum.lt);
        return (Wrapper) this;
    }

    default <VAL> Wrapper le(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, MultiConstant.WhereOptEnum.le);
        return (Wrapper) this;
    }

    default <VAL> Wrapper in(Boolean condition, MultiFunction<T, VAL> prop, VAL... values) {
        this.addWhereTreeData(condition, prop, values, MultiConstant.WhereOptEnum.in);
        return (Wrapper) this;
    }

    default <VAL> Wrapper notIn(Boolean condition, MultiFunction<T, VAL> prop, VAL... values) {
        this.addWhereTreeData(condition, prop, values, MultiConstant.WhereOptEnum.not_in);
        return (Wrapper) this;
    }

    default <VAL> Wrapper likeAll(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(condition, prop, value, MultiConstant.WhereOptEnum.likeAll);
        return (Wrapper) this;
    }

    /**
     * 递归 getSqlWhereProps 拼接最后结果
     *
     * @return where条件中的字段条件信息(拼接后)
     */
    default String getSqlWhereProps(String relationCode, List<WhereDataUnit> extendParams) {
        String sqlWhereProps = getWhereTree().getSqlWhereProps(relationCode);
        if (!MultiUtil.isEmpty(extendParams)) {
            sqlWhereProps += extendParams.stream().map(p -> p.getSqlWhereProps(relationCode)).collect(Collectors.joining("    \nand "));
        }
        return sqlWhereProps;
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
    default <VAL> void addWhereTreeData(Boolean condition, MultiFunction<T, VAL> prop, Object values, MultiConstant.WhereOptEnum opt) {
        if (!condition) {
            return;
        }
        SerializedLambdaData resolve = SerializedLambda.resolveCache(prop);
        if (null == getClassName()) {
            setClassName(MultiUtil.firstToLowerCase(resolve.getClazz().getSimpleName()));
        }
        getWhereTree().getWhereDatas().add(new WhereDataUnit(resolve.getPropName(), opt, values));
    }
}
