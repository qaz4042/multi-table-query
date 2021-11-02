package com.lzb.mpmt.service;

import com.lzb.mpmt.service.common.MultiFunction;
import com.lzb.mpmt.service.common.WhereTreeNode;
import com.lzb.mpmt.service.intf.MultiWrapperLimit;
import com.lzb.mpmt.service.intf.MultiWrapperSelect;
import com.lzb.mpmt.service.intf.MultiWrapperWhere;
import com.lzb.mpmt.service.util.MutilUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSub<SUB> implements
        MultiWrapperWhere<SUB, MultiWrapperSub<SUB>>,
        MultiWrapperSelect<SUB, MultiWrapperSub<SUB>>
{

    //下划线表名
    private String tableName;

    // where id='1' and name like '%张三%   '多个条件 n1 and ( n2 or n3 )
    private WhereTreeNode whereTree = new WhereTreeNode();

    // select id,name,sex
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
