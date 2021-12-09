package com.lzb.mpmt.service.multiwrapper.wrapper.inner;

import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata.WhereDataTree;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSubMainWhereInner<SUB> implements
        MultiWrapperWhere<SUB, MultiWrapperSubMainWhereInner<SUB>>{
    /**
     * 下划线表名
     */
    private String className;

    /**
     * where条件
     */
    private WhereDataTree whereTree = new WhereDataTree();
}
