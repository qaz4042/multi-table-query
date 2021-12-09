package com.lzb.mpmt.service.multiwrapper.wrapper;

import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.lzb.mpmt.service.multiwrapper.wrapper.inner.MultiWrapperMainInner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Administrator
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperMain<MAIN> {

    /**
     * wrapperMainInner
     */
    private MultiWrapperMainInner<MAIN> wrapperMainInner;

    public static <MAIN> MultiWrapperMain<MAIN> lambda(Class<MAIN> clazz) {
        return new MultiWrapperMain<>(MultiWrapperMainInner.lambda(clazz));
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN> select(MultiFunction<MAIN, VAL>... propFuncs) {
        wrapperMainInner.select(propFuncs);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN> desc(MultiFunction<MAIN, VAL>... propFuncs) {
        wrapperMainInner.desc(propFuncs);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN> asc(MultiFunction<MAIN, VAL>... propFuncs) {
        wrapperMainInner.asc(propFuncs);
        return this;
    }

    public MultiWrapperMain<MAIN> count() {
        wrapperMainInner.count();
        return this;
    }

    public MultiWrapperMain<MAIN> sumAll() {
        wrapperMainInner.sumAll();
        return this;
    }

    public MultiWrapperMain<MAIN> aggregateAll(MultiConstant.MultiAggregateTypeEnum... aggregateTypeEnums) {
        wrapperMainInner.aggregateAll(aggregateTypeEnums);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> and(Consumer<MultiWrapperMain<MAIN>> andContent) {
        MultiWrapperMain<MAIN> wrapperMain = new MultiWrapperMain<>(new MultiWrapperMainInner<>());
        andContent.accept(wrapperMain);
        wrapperMainInner.getWhereTree().getWhereDatas().add(wrapperMain.wrapperMainInner.getWhereTree());
        return this;
    }

    /***
     * 一个层级上只要有一个or条件,认定本层所有条件都是or关系
     */
    public MultiWrapperMain<MAIN> or() {
        wrapperMainInner.or();
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> eq(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.eq(prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> isNull(MultiFunction<MAIN, VAL> prop) {
        wrapperMainInner.isNull(prop);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> isNotNull(MultiFunction<MAIN, VAL> prop) {
        wrapperMainInner.isNotNull(prop);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> gt(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.gt(prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> ge(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.ge(prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> lt(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.lt(prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> le(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.le(prop, value);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN> in(MultiFunction<MAIN, VAL> prop, VAL... values) {
        wrapperMainInner.in(prop, values);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN> notIn(MultiFunction<MAIN, VAL> prop, VAL... values) {
        wrapperMainInner.notIn(prop, values);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> likeAll(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.likeAll(prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> eq(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.eq(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> isNull(Boolean condition, MultiFunction<MAIN, VAL> prop) {
        wrapperMainInner.isNull(condition, prop);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> isNotNull(Boolean condition, MultiFunction<MAIN, VAL> prop) {
        wrapperMainInner.isNotNull(condition, prop);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> gt(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.gt(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> ge(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.ge(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> lt(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.lt(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> le(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.le(condition, prop, value);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN> in(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL... values) {
        wrapperMainInner.in(condition, prop, values);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN> notIn(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL... values) {
        wrapperMainInner.notIn(condition, prop, values);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> likeAll(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.likeAll(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> sum(MultiFunction<MAIN, VAL> propFunc) {
        wrapperMainInner.sum(propFunc, null);
        return this;
    }

    /***
     * 设置查询字段列表,不设置则默认*(全查询)
     *
     * @param propFunc 某个字段
     * @param <VAL> 字段泛型
     * @return 当前wrapper
     */
    public <VAL> MultiWrapperMain<MAIN> sum(MultiFunction<MAIN, VAL> propFunc, String alias) {
        wrapperMainInner.sum(propFunc, alias);
        return this;
    }

    /***
     * 聚合
     *
     * @param propFunc 某个字段
     * @param <VAL> 字段泛型
     * @return 当前wrapper
     */
    public <VAL> MultiWrapperMain<MAIN> aggregate(MultiFunction<MAIN, VAL> propFunc, String alias, MultiConstant.MultiAggregateTypeEnum aggregateType) {
        wrapperMainInner.aggregate(propFunc, alias, aggregateType);
        return this;
    }

    public MultiWrapperMain<MAIN> limit(long offset, long size) {
        wrapperMainInner.limit(offset, size);
        return this;
    }

    public MultiWrapperMain<MAIN> limit(long offset, long size, List<String> orderInfos) {
        wrapperMainInner.limit(offset, size, orderInfos);
        return this;
    }
}
