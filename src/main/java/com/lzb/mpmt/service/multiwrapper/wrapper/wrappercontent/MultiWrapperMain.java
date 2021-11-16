package com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent;

import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;
import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant.ClassRelationOneOrManyEnum;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperAggregate;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.aggregate.MultiAggregateInfo;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperLimit;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperSelect;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata.WhereDataTree;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperMain<MAIN> implements
        MultiWrapperWhere<MAIN, MultiWrapperMain<MAIN>>,
        MultiWrapperSelect<MAIN, MultiWrapperMain<MAIN>>,
        MultiWrapperLimit<MAIN, MultiWrapperMain<MAIN>>,
        MultiWrapperAggregate<MAIN, MultiWrapperMain<MAIN>>
        , IMultiWrapperSubAndRelationTreeNode {

    /**
     * 下划线表名
     */
    private String tableName;

    /**
     * where条件
     */
    private WhereDataTree whereTree = new WhereDataTree();

    /**
     * select属性列表
     */
    private List<String> selectFields;

    /**
     * limit分页
     * mysql> SELECT * FROM table LIMIT {limitOffset},{limitSize};   //检索 第limitOffset+1行 到 limitOffset+limitSize行
     * mysql> SELECT * FROM table LIMIT 5,10;                        //检索 第6行 到 第15行
     * mysql> SELECT * FROM table LIMIT 95,-1;                       //检索 第96行 到 第last行.
     * mysql> SELECT * FROM table LIMIT {limitSize};                 //检索 第1行 到 第limitSize行 (limitOffset为空)
     * mysql> SELECT * FROM table LIMIT 5;                           //检索 第1行 到 第5行
     */
    private Long limitOffset;
    private Long limitSize;

    /**
     * 类为了生成List<SUB>
     */
    private Class<MAIN> clazz;

    /**
     * 聚合函数信息 执行MultiExecutor.page()/MultiExecutor.aggregate()时,才会使用到
     */
    private List<MultiAggregateInfo> multiAggregateInfos = Collections.emptyList();

    /**
     * 是否全部按默认字段去聚合
     */
    private List<MultiConstant.MultiAggregateTypeEnum> aggregateAllTypes = new ArrayList<>(4);

    public MultiWrapperMain<MAIN> aggregateAll(MultiConstant.MultiAggregateTypeEnum... aggregateTypeEnums) {
        aggregateAllTypes.addAll(Arrays.asList(aggregateTypeEnums));
        return this;
    }
    public MultiWrapperMain<MAIN> count() {
        aggregateAllTypes.add(MultiConstant.MultiAggregateTypeEnum.COUNT);
        return this;
    }

    public static <MAIN> MultiWrapperMain<MAIN> lambda(Class<MAIN> clazz) {
        String tableName = MultiUtil.camelToUnderline(clazz.getSimpleName());
        MultiWrapperMain<MAIN> wrapperMain = new MultiWrapperMain<>();
        wrapperMain.setTableName(tableName);
        wrapperMain.setClazz(clazz);
        return wrapperMain;
    }

    @SafeVarargs
    @Override
    public final <VAL> MultiWrapperMain<MAIN> select(MultiFunction<MAIN, VAL>... propFuncs) {
        return MultiWrapperSelect.super.select(propFuncs);
    }

    @Override
    public String getTableNameThis() {
        return getTableName();
    }

    @Override
    public String getTableNameOther() {
        return "";
    }

    @Override
    public ClassRelationOneOrManyEnum getTableNameThisOneOrMany() {
        return null;
    }

    @Override
    public Boolean getTableNameOtherRequire() {
        return false;
    }

    @Override
    public Class<?> getTableClassThis() {
        return getClazz();
    }

    @Override
    public String getRelationCode() {
        return getTableName();
    }

    @Override
    public MultiWrapperSelect<?, ?> getMultiWrapperSelectInfo() {
        return this;
    }
}
