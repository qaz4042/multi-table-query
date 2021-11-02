package com.lzb.mpmt.service;

import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperLimit;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperSelect;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import com.lzb.mpmt.service.multiwrapper.util.MutilUtil;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata.WhereDataTree;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        MultiWrapperLimit<MAIN, MultiWrapperMain<MAIN>>
{

    /** 下划线表名 */
    private String tableName;

    /** where条件 */
    private WhereDataTree whereTree = new WhereDataTree();

    /** select属性列表 */
    private List<String> selectProps;

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

    public static <MAIN> MultiWrapperMain<MAIN> lambda(Class<MAIN> clazz) {
        MultiWrapperMain<MAIN> wrapperMain = new MultiWrapperMain<>();
        wrapperMain.setTableName(MutilUtil.camelToUnderline(clazz.getSimpleName()));
        return wrapperMain;
    }

    @SafeVarargs
    @Override
    public final <VAL> MultiWrapperMain<MAIN> select(MultiFunction<MAIN, VAL>... propFuncs) {
        return MultiWrapperSelect.super.select(propFuncs);
    }
}
