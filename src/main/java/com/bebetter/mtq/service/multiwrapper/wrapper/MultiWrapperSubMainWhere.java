package com.bebetter.mtq.service.multiwrapper.wrapper;

import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.bebetter.mtq.service.multiwrapper.wrapper.inner.MultiWrapperSubMainWhereInner;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSubMainWhere<SUB> {
    MultiWrapperSubMainWhere(MultiWrapperSubMainWhereInner<SUB> wrapperSubMainWhereInner) {
        this.wrapperSubMainWhereInner = wrapperSubMainWhereInner;
    }

    /**
     * wrapperSubMainWhereInner
     */
    private MultiWrapperSubMainWhereInner<SUB> wrapperSubMainWhereInner;

    /***
     * 一个层级上只要有一个or条件,认定本层所有条件都是or关系
     */
    public MultiWrapperSubMainWhere<SUB> or() {
        wrapperSubMainWhereInner.or();
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> eq(MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.eq(prop, value);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> isNull(MultiFunction<SUB, VAL> prop) {
        wrapperSubMainWhereInner.isNull(prop);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> isNotNull(MultiFunction<SUB, VAL> prop) {
        wrapperSubMainWhereInner.isNotNull(prop);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> gt(MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.gt(prop, value);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> ge(MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.ge(prop, value);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> lt(MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.lt(prop, value);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> le(MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.le(prop, value);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperSubMainWhere<SUB> in(MultiFunction<SUB, VAL> prop, VAL... values) {
        wrapperSubMainWhereInner.in(prop, values);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperSubMainWhere<SUB> notIn(MultiFunction<SUB, VAL> prop, VAL... values) {
        wrapperSubMainWhereInner.notIn(prop, values);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> likeAll(MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.likeAll(prop, value);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> eq(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.eq(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> isNull(Boolean condition, MultiFunction<SUB, VAL> prop) {
        wrapperSubMainWhereInner.isNull(condition, prop);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> isNotNull(Boolean condition, MultiFunction<SUB, VAL> prop) {
        wrapperSubMainWhereInner.isNotNull(condition, prop);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> gt(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.gt(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> ge(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.ge(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> lt(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.lt(condition, prop, value);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> le(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.le(condition, prop, value);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperSubMainWhere<SUB> in(Boolean condition, MultiFunction<SUB, VAL> prop, VAL... values) {
        wrapperSubMainWhereInner.in(condition, prop, values);
        return this;
    }

    @SafeVarargs
    public final <VAL> MultiWrapperSubMainWhere<SUB> notIn(Boolean condition, MultiFunction<SUB, VAL> prop, VAL... values) {
        wrapperSubMainWhereInner.notIn(condition, prop, values);
        return this;
    }

    public <VAL> MultiWrapperSubMainWhere<SUB> likeAll(Boolean condition, MultiFunction<SUB, VAL> prop, VAL value) {
        wrapperSubMainWhereInner.likeAll(condition, prop, value);
        return this;
    }

}
