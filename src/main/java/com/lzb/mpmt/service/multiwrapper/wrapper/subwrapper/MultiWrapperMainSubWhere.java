package com.lzb.mpmt.service.multiwrapper.wrapper.subwrapper;

import com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata.WhereDataTree;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperMainSubWhere<MAIN> implements MultiWrapperWhere<MAIN, MultiWrapperMainSubWhere<MAIN>> {


    /** 下划线表名 */
    private String tableName;

    /** where条件 */
    private WhereDataTree whereTree = new WhereDataTree();

    public static <MAIN> MultiWrapperMainSubWhere<MAIN> lambda(Class<MAIN> clazz) {
        return new MultiWrapperMainSubWhere<>();
    }
}
