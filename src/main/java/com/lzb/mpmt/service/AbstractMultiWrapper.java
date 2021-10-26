package com.lzb.mpmt.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lzb.mpmt.service.common.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.function.Consumer;

@Data
@NoArgsConstructor
@SuppressWarnings({"unused", "unchecked"})
public class AbstractMultiWrapper<T, Wrapper extends AbstractMultiWrapper<T, Wrapper>> {
    private String tableName;
    // 多个条件 n1 and ( n2 or n3 )
    private WhereTreeNode whereTree = new WhereTreeNode();

    @SneakyThrows
    public <VAL> Wrapper and(Consumer<Wrapper> andContent) {
        //noinspection unchecked
        Wrapper wrapper = (Wrapper) this.getClass().newInstance();
        andContent.accept(wrapper);
        whereTree.getWhereTreeData().add(wrapper.getWhereTree());
        return wrapper;
    }

    public <VAL> Wrapper or() {
        whereTree.setAndOr(WhereAndOrEnum.or);
        return (Wrapper) this;
    }

    public <VAL> Wrapper eq(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.eq);
        return (Wrapper) this;
    }

    public <VAL> Wrapper gt(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.gt);
        return (Wrapper) this;
    }

    public <VAL> Wrapper ge(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.ge);
        return (Wrapper) this;
    }

    public <VAL> Wrapper lt(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.lt);
        return (Wrapper) this;
    }

    public <VAL> Wrapper le(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.le);
        return (Wrapper) this;
    }

    public <VAL> Wrapper in(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.in);
        return (Wrapper) this;
    }

    public <VAL> Wrapper notIn(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.not_in);
        return (Wrapper) this;
    }

    public <VAL> Wrapper like(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.like);
        return (Wrapper) this;
    }

    private <VAL> void addWhereTreeData(MultiFunction<T, VAL> prop, VAL value, WhereOptEnum opt) {
        SerializedLambdaData resolve = SerializedLambda.resolveCache(prop);
        if (null == getTableName()) {
            tableName = resolve.getClazzNameUnderline();
        }
        String propNameUnderline = resolve.getPropNameUnderline();
        whereTree.getWhereTreeData().add(new WhereTreeNodeData(propNameUnderline, opt, value));
    }

}
