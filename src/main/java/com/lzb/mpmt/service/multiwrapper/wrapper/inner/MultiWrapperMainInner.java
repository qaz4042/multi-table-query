package com.lzb.mpmt.service.multiwrapper.wrapper.inner;

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
public class MultiWrapperMainInner<MAIN> implements
        MultiWrapperWhere<MAIN, MultiWrapperMainInner<MAIN>>,
        MultiWrapperSelect<MAIN, MultiWrapperMainInner<MAIN>>,
        MultiWrapperLimit<MAIN, MultiWrapperMainInner<MAIN>>,
        MultiWrapperAggregate<MAIN, MultiWrapperMainInner<MAIN>>
        , IMultiWrapperSubAndRelationTreeNode {

    /**
     * 下划线表名
     */
    private String className;

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
     * 排序信息
     * 例如 ['username asc','id desc'] 或者 ['userStaff.username asc','userStaff.id desc'] (暂时只支持主表字段排序)
     */
    private List<String> orderInfos = Collections.emptyList();

    /**
     * 类为了生成List<SUB>
     */
    private Class<MAIN> clazz;

    /**
     * 是否全部按默认字段去聚合
     */
    private List<MultiConstant.MultiAggregateTypeEnum> aggregateAllTypes = new ArrayList<>(4);
    private List<MultiConstant.MultiAggregateTypeEnum> aggregateAllTypesTemp = new ArrayList<>(4);

    /**
     * 聚合函数信息 执行MultiExecutor.page()/MultiExecutor.aggregate()时,才会使用到
     */
    private List<MultiAggregateInfo> aggregateInfos = Collections.emptyList();
    private List<MultiAggregateInfo> aggregateInfosTemp = Collections.emptyList();


    public MultiWrapperMainInner<MAIN> aggregateAll(MultiConstant.MultiAggregateTypeEnum... aggregateTypeEnums) {
        aggregateAllTypes.addAll(Arrays.asList(aggregateTypeEnums));
        return this;
    }

    public MultiWrapperMainInner<MAIN> count() {
        if (!aggregateAllTypes.contains(MultiConstant.MultiAggregateTypeEnum.COUNT)) {
            aggregateAllTypes.add(MultiConstant.MultiAggregateTypeEnum.COUNT);
        }
        return this;
    }

    public MultiWrapperMainInner<MAIN> sumAll() {
        if (!aggregateAllTypes.contains(MultiConstant.MultiAggregateTypeEnum.SUM)) {
            aggregateAllTypes.add(MultiConstant.MultiAggregateTypeEnum.SUM);
        }
        return this;
    }

    public static <MAIN> MultiWrapperMainInner<MAIN> lambda(Class<MAIN> clazz) {
        String className = MultiUtil.firstToLowerCase(clazz.getSimpleName());
        MultiWrapperMainInner<MAIN> wrapperMain = new MultiWrapperMainInner<>();
        wrapperMain.setClassName(className);
        wrapperMain.setClazz(clazz);
        return wrapperMain;
    }

    @SafeVarargs
    @Override
    public final <VAL> MultiWrapperMainInner<MAIN> select(MultiFunction<MAIN, VAL>... propFuncs) {
        return MultiWrapperSelect.super.select(propFuncs);
    }

    @Override
    public String getClassNameThis() {
        return getClassName();
    }

    @Override
    public String getClassNameOther() {
        return MultiConstant.Strings.EMPTY;
    }

    @Override
    public ClassRelationOneOrManyEnum getClassNameThisOneOrMany() {
        return null;
    }

    @Override
    public Boolean getClassNameOtherRequire() {
        return false;
    }

    @Override
    public Class<?> getTableClassThis() {
        return getClazz();
    }

    @Override
    public String getRelationCode() {
        return getClassName();
    }

    @Override
    public MultiWrapperSelect<?, ?> getMultiWrapperSelectInfo() {
        return this;
    }

    @Override
    @SafeVarargs
    public final <VAL> MultiWrapperMainInner<MAIN> desc(MultiFunction<MAIN, VAL>... propFuncs) {
        //为了添加 @SafeVarargs 重写的方法
        return MultiWrapperLimit.super.desc(propFuncs);
    }

    @Override
    @SafeVarargs
    public final <VAL> MultiWrapperMainInner<MAIN> asc(MultiFunction<MAIN, VAL>... propFuncs) {
        return MultiWrapperLimit.super.asc(propFuncs);
    }

    public void aggregateBackupAndClear() {
        aggregateAllTypesTemp = aggregateAllTypes;
        aggregateInfosTemp = aggregateInfos;
        aggregateAllTypes = Collections.emptyList();
        aggregateInfos = Collections.emptyList();
    }

    public void aggregateRestore() {
        aggregateAllTypes = aggregateAllTypesTemp;
        aggregateInfos = aggregateInfosTemp;
    }
}
