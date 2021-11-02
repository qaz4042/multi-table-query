package com.lzb.mpmt.service.multiwrapper.sqlsegment;

import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.SerializedLambda;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.SerializedLambdaData;
import com.lzb.mpmt.service.multiwrapper.enums.WhereAndOrEnum;
import com.lzb.mpmt.service.multiwrapper.enums.WhereOptEnum;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata.WhereTreeNode;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata.WhereTreeNodeData;
import lombok.SneakyThrows;

import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@SuppressWarnings({"unused", "unchecked"})
public interface MultiWrapperWhere<T, Wrapper extends MultiWrapperWhere<T, Wrapper>> {
//    private String tableName;
//    private WhereTreeNode whereTree = new WhereTreeNode();    // 多个条件 n1 and ( n2 or n3 )

    String getTableName();
    void setTableName(String tableName);

    WhereTreeNode getWhereTree();

    @SneakyThrows
    default <VAL> Wrapper and(Consumer<Wrapper> andContent) {
        // noinspection deprecation
        Wrapper wrapper = (Wrapper) this.getClass().newInstance();
        andContent.accept(wrapper);
        getWhereTree().getWhereTreeDatas().add(wrapper.getWhereTree());
        return (Wrapper) this;
    }

    default <VAL> Wrapper or() {
        getWhereTree().setAndOr(WhereAndOrEnum.or);
        return (Wrapper) this;
    }

    default <VAL> Wrapper eq(MultiFunction<T, VAL> prop, VAL value) {
        return eq(true, prop, value);
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

    default String getSqlWhereProps() {
        WhereTreeNode whereTree = getWhereTree();
        String tableName = getTableName();
        return whereTree.getWhereTreeDatas().stream().map(data -> data.getSqlWhereProps(tableName)).collect(Collectors.joining("\n" + whereTree.getAndOr().name() + " "));
    }

    private <VAL> void addWhereTreeData(Boolean condition, MultiFunction<T, VAL> prop, Object values, WhereOptEnum opt) {
        if (!condition) {
            return;
        }
        SerializedLambdaData resolve = SerializedLambda.resolveCache(prop);
        if (null == getTableName()) {
            setTableName(resolve.getClazzNameUnderline());
        }
        String propNameUnderline = resolve.getPropNameUnderline();
        getWhereTree().getWhereTreeDatas().add(new WhereTreeNodeData(propNameUnderline, opt, values));
    }
}
