package com.lzb.mpmt.service.multiwrapper.wrapper.wrappercontent;

import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperAggregate;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperSelect;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.aggregate.MultiAggregateInfo;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata.WhereDataTree;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSub<SUB> implements
        MultiWrapperWhere<SUB, MultiWrapperSub<SUB>>,
        MultiWrapperSelect<SUB, MultiWrapperSub<SUB>>,
        MultiWrapperAggregate<SUB, MultiWrapperSub<SUB>> {


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
     * 类为了生成List<SUB>
     */
    private Class<SUB> clazz;

    private MultiWrapperSubMainWhere<?> mainWhere;

    /**
     * 聚合函数信息 执行MultiExecutor.page()/MultiExecutor.aggregate()时,才会使用到
     */
    private List<MultiAggregateInfo> multiAggregateInfos = new ArrayList<>(8);

    public static <SUB, MAIN_WHERE extends MultiWrapperSubMainWhere<SUB>> MultiWrapperSub<SUB> lambda(Class<SUB> clazz) {
        String className = MultiUtil.firstToLowerCase(clazz.getSimpleName());
        MultiWrapperSub<SUB> wrapperSub = new MultiWrapperSub<>();
        wrapperSub.setClassName(className);
        wrapperSub.setClazz(clazz);
        return wrapperSub;
    }

    @SafeVarargs
    @Override
    public final <VAL> MultiWrapperSub<SUB> select(MultiFunction<SUB, VAL>... propFuncs) {
        return MultiWrapperSelect.super.select(propFuncs);
    }


    public <VAL,MAIN_WHERE extends MultiWrapperSubMainWhere<SUB>> MultiWrapperSub<SUB> mainWhere(Consumer<MAIN_WHERE> mainWhereConsumer) {
        if (this.mainWhere == null) {
            this.mainWhere = new MultiWrapperSubMainWhere<SUB>();
        }
        //noinspection unchecked
        mainWhereConsumer.accept((MAIN_WHERE) mainWhere);
        return this;
    }
}
