package com.bebetter.mtq.service.multiwrapper.wrapper;

import com.bebetter.mtq.service.multiwrapper.constant.MultiConstant;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.bebetter.mtq.service.multiwrapper.wrapper.inner.MultiWrapperMainInner;
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
public class MultiWrapperMain<MAIN, DTO> {

    /**
     * wrapperMainInner
     */
    private MultiWrapperMainInner<MAIN, DTO> wrapperMainInner;

    public static <MAIN, DTO> MultiWrapperMain<MAIN, DTO> lambda(Class<MAIN> clazz, Class<DTO> clazzDto) {
        return new MultiWrapperMain<>(MultiWrapperMainInner.lambda(clazz, clazzDto));
    }

    public static <MAIN> MultiWrapperMain<MAIN, MAIN> lambda(Class<MAIN> clazz) {
        return new MultiWrapperMain<>(MultiWrapperMainInner.lambda(clazz, clazz));
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN, DTO> select(MultiFunction<MAIN, VAL>... propFuncs) {
        wrapperMainInner.select(propFuncs);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN, DTO> desc(MultiFunction<MAIN, VAL>... propFuncs) {
        wrapperMainInner.desc(propFuncs);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN, DTO> asc(MultiFunction<MAIN, VAL>... propFuncs) {
        wrapperMainInner.asc(propFuncs);
        return this;
    }

    public MultiWrapperMain<MAIN, DTO> count() {
        wrapperMainInner.count();
        return this;
    }

    public MultiWrapperMain<MAIN, DTO> sumAll() {
        wrapperMainInner.sumAll();
        return this;
    }

    public MultiWrapperMain<MAIN, DTO> aggregateAll(MultiConstant.MultiAggregateTypeEnum... aggregateTypeEnums) {
        wrapperMainInner.aggregateAll(aggregateTypeEnums);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> and(Consumer<MultiWrapperMain<MAIN, DTO>> andContent) {
        MultiWrapperMain<MAIN, DTO> wrapperMain = new MultiWrapperMain<>(new MultiWrapperMainInner<>());
        andContent.accept(wrapperMain);
        wrapperMainInner.getWhereTree().getWhereDatas().add(wrapperMain.wrapperMainInner.getWhereTree());
        return this;
    }

    /***
     * 一个层级上只要有一个or条件,认定本层所有条件都是or关系
     */
    public MultiWrapperMain<MAIN, DTO> or() {
        wrapperMainInner.or();
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> eq(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.eq(prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> isNull(MultiFunction<MAIN, VAL> prop) {
        wrapperMainInner.isNull(prop);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> isNotNull(MultiFunction<MAIN, VAL> prop) {
        wrapperMainInner.isNotNull(prop);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> gt(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.gt(prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> ge(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.ge(prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> lt(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.lt(prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> le(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.le(prop, value);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN, DTO> in(MultiFunction<MAIN, VAL> prop, VAL... values) {
        wrapperMainInner.in(prop, values);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN, DTO> notIn(MultiFunction<MAIN, VAL> prop, VAL... values) {
        wrapperMainInner.notIn(prop, values);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> likeAll(MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.likeAll(prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> eq(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.eq(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> isNull(Boolean condition, MultiFunction<MAIN, VAL> prop) {
        wrapperMainInner.isNull(condition, prop);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> isNotNull(Boolean condition, MultiFunction<MAIN, VAL> prop) {
        wrapperMainInner.isNotNull(condition, prop);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> gt(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.gt(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> ge(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.ge(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> lt(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.lt(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> le(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.le(condition, prop, value);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN, DTO> in(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL... values) {
        wrapperMainInner.in(condition, prop, values);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperMain<MAIN, DTO> notIn(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL... values) {
        wrapperMainInner.notIn(condition, prop, values);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> likeAll(Boolean condition, MultiFunction<MAIN, VAL> prop, VAL value) {
        wrapperMainInner.likeAll(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN, DTO> sum(MultiFunction<MAIN, VAL> propFunc) {
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
    public <VAL> MultiWrapperMain<MAIN, DTO> sum(MultiFunction<MAIN, VAL> propFunc, String alias) {
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
    public <VAL> MultiWrapperMain<MAIN, DTO> aggregate(MultiFunction<MAIN, VAL> propFunc, String alias, MultiConstant.MultiAggregateTypeEnum aggregateType) {
        wrapperMainInner.aggregate(propFunc, alias, aggregateType);
        return this;
    }

    public MultiWrapperMain<MAIN, DTO> limit(long offset, long size) {
        wrapperMainInner.limit(offset, size);
        return this;
    }

    public MultiWrapperMain<MAIN, DTO> limit(long offset, long size, List<String> orderInfos) {
        wrapperMainInner.limit(offset, size, orderInfos);
        return this;
    }
}
