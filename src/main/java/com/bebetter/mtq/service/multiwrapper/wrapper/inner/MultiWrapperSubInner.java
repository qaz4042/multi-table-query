package com.bebetter.mtq.service.multiwrapper.wrapper.inner;

import com.bebetter.mtq.service.multiwrapper.sqlsegment.MultiWrapperAggregate;
import com.bebetter.mtq.service.multiwrapper.sqlsegment.MultiWrapperSelect;
import com.bebetter.mtq.service.multiwrapper.sqlsegment.aggregate.MultiAggregateInfo;
import com.bebetter.mtq.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import com.bebetter.mtq.service.multiwrapper.sqlsegment.wheredata.WhereDataTree;
import com.bebetter.mtq.service.multiwrapper.util.MultiUtil;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.MultiFunction;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSubInner<SUB> implements
        MultiWrapperWhere<SUB, MultiWrapperSubInner<SUB>>,
        MultiWrapperSelect<SUB, MultiWrapperSubInner<SUB>>,
        MultiWrapperAggregate<SUB, MultiWrapperSubInner<SUB>> {


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

    private MultiWrapperSubMainWhereInner<SUB> mainWhere;

    /**
     * 聚合函数信息 执行MultiExecutor.page()/MultiExecutor.aggregate()时,才会使用到
     */
    private List<MultiAggregateInfo> aggregateInfos = new ArrayList<>(8);

    public static <SUB, MAIN_WHERE extends MultiWrapperSubMainWhereInner<SUB>> MultiWrapperSubInner<SUB> lambda(Class<SUB> clazz) {
        String className = MultiUtil.firstToLowerCase(clazz.getSimpleName());
        MultiWrapperSubInner<SUB> wrapperSub = new MultiWrapperSubInner<>();
        wrapperSub.setClassName(className);
        wrapperSub.setClazz(clazz);
        return wrapperSub;
    }

    @SafeVarargs
    @Override
    public final <VAL> MultiWrapperSubInner<SUB> select(MultiFunction<SUB, VAL>... propFuncs) {
        return MultiWrapperSelect.super.select(propFuncs);
    }


    public <VAL,MAIN_WHERE extends MultiWrapperSubMainWhereInner<SUB>> MultiWrapperSubInner<SUB> mainWhere(Consumer<MAIN_WHERE> mainWhereConsumer) {
        if (this.mainWhere == null) {
            this.mainWhere = new MultiWrapperSubMainWhereInner<>();
        }
        //noinspection unchecked
        mainWhereConsumer.accept((MAIN_WHERE) mainWhere);
        return this;
    }
}
