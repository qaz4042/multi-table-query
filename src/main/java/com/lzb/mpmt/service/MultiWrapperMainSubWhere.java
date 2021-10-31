package com.lzb.mpmt.service;

import com.lzb.mpmt.service.common.WhereTreeNode;
import com.lzb.mpmt.service.intf.MultiWrapperLimit;
import com.lzb.mpmt.service.intf.MultiWrapperSelect;
import com.lzb.mpmt.service.intf.MultiWrapperWhere;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class
MultiWrapperMainSubWhere<MAIN> implements
        MultiWrapperWhere<MAIN, MultiWrapperMainSubWhere<MAIN>>
{


    //下划线表名
    private String tableName;

    // where id='1' and name like '%张三%   '多个条件 n1 and ( n2 or n3 )
    private WhereTreeNode whereTree = new WhereTreeNode();

    public static <MAIN> MultiWrapperMainSubWhere<MAIN> lambda(Class<MAIN> clazz) {
        return new MultiWrapperMainSubWhere<>();
    }
}
