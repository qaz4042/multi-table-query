package com.lzb.mpmt.service;

import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata.WhereDataTree;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperSelect;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import com.lzb.mpmt.service.multiwrapper.util.MutilUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSub<SUB> implements
        MultiWrapperWhere<SUB, MultiWrapperSub<SUB>>,
        MultiWrapperSelect<SUB, MultiWrapperSub<SUB>>
{

    /** 下划线表名 */
    private String tableName;

    /** where条件 */
    private WhereDataTree whereTree = new WhereDataTree();

    /** select属性列表 */
    private List<String> selectProps;


    public static <SUB> MultiWrapperSub<SUB> lambda(Class<SUB> clazz) {
        MultiWrapperSub<SUB> wrapperSub = new MultiWrapperSub<>();
        wrapperSub.setTableName(MutilUtil.camelToUnderline(clazz.getSimpleName()));
        return wrapperSub;
    }

    @SafeVarargs
    @Override
    public final <VAL> MultiWrapperSub<SUB> select(MultiFunction<SUB, VAL>... propFuncs) {
        return MultiWrapperSelect.super.select(propFuncs);
    }
}
