package com.lzb.mpmt.service;

import com.lzb.mpmt.service.common.*;
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
public class MultiWrapperMain<MAIN> implements
        MultiWrapperWhere<MAIN, MultiWrapperMain<MAIN>>,
        MultiWrapperSelect<MAIN, MultiWrapperMain<MAIN>>,
        MultiWrapperLimit<MAIN, MultiWrapperMain<MAIN>>
{

    //下划线表名
    private String tableName;

    // where id='1' and name like '%张三%   '多个条件 n1 and ( n2 or n3 )
    private WhereTreeNode whereTree = new WhereTreeNode();

    // select id,name,sex
    private List<String> selectProps;

    //    mysql> SELECT * FROM table LIMIT {limitOffset},{limitSize};   //检索 第limitOffset+1行 到 limitOffset+limitSize行
//    mysql> SELECT * FROM table LIMIT 5,10;                        //检索 第6行 到 第15行
//    mysql> SELECT * FROM table LIMIT 95,-1;                       //检索 第96行 到 第last行.
//    mysql> SELECT * FROM table LIMIT {limitSize};                 //检索 第1行 到 第limitSize行 (limitOffset为空)
//    mysql> SELECT * FROM table LIMIT 5;                           //检索 第1行 到 第5行
    private Long limitOffset;
    private Long limitSize;

    public static <MAIN> MultiWrapperMain<MAIN> lambda(Class<MAIN> clazz) {
        MultiWrapperMain<MAIN> wrapperMain = new MultiWrapperMain<>();
        wrapperMain.setTableName(MutilUtil.camelToUnderline(clazz.getSimpleName()));
        return wrapperMain;
    }


    // List<propName opt values> 子表在主SQL下的的条件(与子表在子表sql下的where条件区分开)
//    private List<MultiWrapperWhere> subTableWhereTrees = new ArrayList<>();

    @SafeVarargs
    @Override
    public final <VAL> MultiWrapperMain<MAIN> select(MultiFunction<MAIN, VAL>... propFuncs) {
        return MultiWrapperSelect.super.select(propFuncs);
    }
}
