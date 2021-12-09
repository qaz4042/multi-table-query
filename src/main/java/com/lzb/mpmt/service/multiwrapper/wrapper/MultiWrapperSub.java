package com.lzb.mpmt.service.multiwrapper.wrapper;

import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.lzb.mpmt.service.multiwrapper.wrapper.inner.MultiWrapperSubInner;
import com.lzb.mpmt.service.multiwrapper.wrapper.inner.MultiWrapperSubMainWhereInner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.function.Consumer;

/**
 * @author Administrator
 */
@Getter
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSub<SUB> {
    MultiWrapperSub(MultiWrapperSubInner<SUB> wrapperSubInner) {
        this.wrapperSubInner = wrapperSubInner;
    }

    /**
     * wrapperSubInner
     */
    private MultiWrapperSubInner<SUB> wrapperSubInner;

    public static <SUB, SUB_WHERE extends MultiWrapperSubMainWhereInner<SUB>> MultiWrapperSub<SUB> lambda(Class<SUB> clazz) {
        return new MultiWrapperSub<>(MultiWrapperSubInner.lambda(clazz));
    }

    @SafeVarargs
    public final <VAL> MultiWrapperSub<SUB> select(MultiFunction<SUB, VAL>... propFuncs) {
        this.wrapperSubInner.select(propFuncs);
        return this;
    }

    @SneakyThrows
    public MultiWrapperSub<SUB> and(Consumer<MultiWrapperSub<SUB>> andContent) {
        MultiWrapperSub<SUB> wrapper = new MultiWrapperSub<>(new MultiWrapperSubInner<>());
        andContent.accept(wrapper);
        this.wrapperSubInner.getWhereTree().getWhereDatas().add(wrapper.wrapperSubInner.getWhereTree());
        return this;
    }


    /***
     * 一个层级上只要有一个or条件,认定本层所有条件都是or关系
     */
    public MultiWrapperSub<SUB> or() {
        this.wrapperSubInner.or();
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> eq(MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.eq(prop, value);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> isNull(MultiFunction<SUB, VAL> prop) {
        this.wrapperSubInner.isNull(prop);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> isNotNull(MultiFunction<SUB, VAL> prop) {
        this.wrapperSubInner.isNotNull(prop);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> gt(MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.gt(prop, value);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> ge(MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.ge(prop, value);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> lt(MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.lt(prop, value);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> le(MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.le(prop, value);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperSub<SUB> in(MultiFunction<SUB, VAL> prop, VAL... values) {
        this.wrapperSubInner.in(prop, values);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperSub<SUB> notIn(MultiFunction<SUB, VAL> prop, VAL... values) {
        this.wrapperSubInner.notIn(prop, values);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> likeAll(MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.likeAll(prop, value);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> eq(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.eq(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> isNull(Boolean condition, MultiFunction<SUB, VAL> prop) {
        this.wrapperSubInner.isNull(condition, prop);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> isNotNull(Boolean condition, MultiFunction<SUB, VAL> prop) {
        this.wrapperSubInner.isNotNull(condition, prop);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> gt(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.gt(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> ge(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.ge(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> lt(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.lt(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> le(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.le(condition, prop, value);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperSub<SUB> in(Boolean condition, MultiFunction<SUB, VAL> prop, VAL... values) {
        this.wrapperSubInner.in(condition, prop, values);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperSub<SUB> notIn(Boolean condition, MultiFunction<SUB, VAL> prop, VAL... values) {
        this.wrapperSubInner.notIn(condition, prop, values);
        return this;
    }

    public <VAL> MultiWrapperSub<SUB> likeAll(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        this.wrapperSubInner.likeAll(condition, prop, value);
        return this;
    }

    public <VAL, SUB_WHERE extends MultiWrapperSubMainWhere<SUB>> MultiWrapperSub<SUB> mainWhere(Consumer<SUB_WHERE> mainWhereConsumer) {
        MultiWrapperSubMainWhereInner<SUB> mainWhere = wrapperSubInner.getMainWhere();
        if (mainWhere == null) {
            mainWhere = new MultiWrapperSubMainWhereInner<>();
            wrapperSubInner.setMainWhere(mainWhere);
        }
        //noinspection unchecked
        mainWhereConsumer.accept((SUB_WHERE) new MultiWrapperSubMainWhere<>(mainWhere));
        return this;
    }


    public <VAL> MultiWrapperSub<SUB> sum(MultiFunction<SUB, VAL> propFunc) {
        this.wrapperSubInner.sum(propFunc, null);
        return this;
    }

    /***
     * 设置查询字段列表,不设置则默认*(全查询)
     *
     * @param propFunc 某个字段
     * @param <VAL> 字段泛型
     * @return 当前wrapper
     */
    public <VAL> MultiWrapperSub<SUB> sum(MultiFunction<SUB, VAL> propFunc, String alias) {
        this.wrapperSubInner.sum(propFunc, alias);
        return this;
    }

    /***
     * 聚合
     *
     * @param propFunc 某个字段
     * @param <VAL> 字段泛型
     * @return 当前wrapper
     */
    public <VAL> MultiWrapperSub<SUB> aggregate(MultiFunction<SUB, VAL> propFunc, String alias, MultiConstant.MultiAggregateTypeEnum aggregateType) {
        this.wrapperSubInner.aggregate(propFunc, alias, aggregateType);
        return this;
    }
}
