package com.lzb.mpmt.service.intf;

import com.lzb.mpmt.service.common.*;
import lombok.SneakyThrows;

import java.util.function.Consumer;
import java.util.stream.Collectors;


@SuppressWarnings({"unused", "unchecked"})
public interface MultiWrapperWhere<T, Wrapper extends MultiWrapperWhere<T, Wrapper>> {
//    private String tableName;
//    // 多个条件 n1 and ( n2 or n3 )
//    private WhereTreeNode whereTree = new WhereTreeNode();

    String getTableName();
    void setTableName(String tableName);

    WhereTreeNode getWhereTree();

    @SneakyThrows
    default <VAL> Wrapper and(Consumer<Wrapper> andContent) {
        //noinspection unchecked
        Wrapper wrapper = (Wrapper) this.getClass().newInstance();
        andContent.accept(wrapper);
        getWhereTree().getWhereTreeDatas().add(wrapper.getWhereTree());
        return (Wrapper) this;
    }

    default <VAL> Wrapper or() {
        getWhereTree().setAndOr(WhereAndOrEnum.or);
        return (Wrapper) this;
    }

    default <VAL> Wrapper eq(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.eq);
        return (Wrapper) this;
    }

    default <VAL> Wrapper gt(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.gt);
        return (Wrapper) this;
    }

    default <VAL> Wrapper ge(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.ge);
        return (Wrapper) this;
    }

    default <VAL> Wrapper lt(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.lt);
        return (Wrapper) this;
    }

    default <VAL> Wrapper le(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.le);
        return (Wrapper) this;
    }

    default <VAL> Wrapper in(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.in);
        return (Wrapper) this;
    }

    default <VAL> Wrapper notIn(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.not_in);
        return (Wrapper) this;
    }

    default <VAL> Wrapper likeDefault(Boolean condition, MultiFunction<T, VAL> prop, VAL value) {
        this.addWhereTreeData(prop, value, WhereOptEnum.likeDefault);
        return (Wrapper) this;
    }

    default String getSqlWhere() {
        return " where " + getWhereSqlRecursion(getTableName(), getWhereTree());
    }

    private <VAL> void addWhereTreeData(MultiFunction<T, VAL> prop, VAL value, WhereOptEnum opt) {
        SerializedLambdaData resolve = SerializedLambda.resolveCache(prop);
        if (null == getTableName()) {
            setTableName(resolve.getClazzNameUnderline());
        }
        String propNameUnderline = resolve.getPropNameUnderline();
        getWhereTree().getWhereTreeDatas().add(new WhereTreeNodeData(propNameUnderline, opt, value));
    }

    private String getWhereSqlRecursion(String tableName, WhereTreeNode whereTree) {
        return whereTree.getWhereTreeDatas().stream().map(data -> data.toSql(tableName)).collect(Collectors.joining(" " + whereTree.getAndOr().name() + " "));
    }

}
