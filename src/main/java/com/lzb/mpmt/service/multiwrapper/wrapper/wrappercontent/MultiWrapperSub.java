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

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSub<SUB, MAIN_WHERE extends MultiWrapperSubMainWhere<SUB>> implements
        MultiWrapperWhere<SUB, MultiWrapperSub<SUB, MAIN_WHERE>>,
        MultiWrapperSelect<SUB, MultiWrapperSub<SUB, MAIN_WHERE>>,
        MultiWrapperAggregate<SUB, MultiWrapperMain<SUB>> {


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
     * 类为了生成List<SUB>
     */
    private Class<SUB> clazz;

    private MAIN_WHERE mainWhere;

    /**
     * 聚合函数信息 执行MultiExecutor.page()/MultiExecutor.aggregate()时,才会使用到
     */
    private List<MultiAggregateInfo> multiAggregateInfos = Collections.emptyList();

    public static <SUB, MAIN_WHERE extends MultiWrapperSubMainWhere<SUB>> MultiWrapperSub<SUB, MAIN_WHERE> lambda(Class<SUB> clazz) {
        String tableName = MultiUtil.camelToUnderline(clazz.getSimpleName());
        MultiWrapperSub<SUB, MAIN_WHERE> wrapperSub = new MultiWrapperSub<>();
        wrapperSub.setTableName(tableName);
        wrapperSub.setClazz(clazz);
        return wrapperSub;
    }

    @SafeVarargs
    @Override
    public final <VAL> MultiWrapperSub<SUB, MAIN_WHERE> select(MultiFunction<SUB, VAL>... propFuncs) {
        return MultiWrapperSelect.super.select(propFuncs);
    }


    public <VAL> MultiWrapperSub<SUB, MAIN_WHERE> mainWhere(Consumer<MAIN_WHERE> mainWhereConsumer) {
        //noinspection unchecked
        MAIN_WHERE mainWhere = (MAIN_WHERE) new MultiWrapperSubMainWhere<SUB>();
        mainWhereConsumer.accept(mainWhere);
        return this;
    }
}
