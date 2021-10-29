package com.lzb.mpmt.service;

import com.lzb.mpmt.service.common.MultiFunction;
import com.lzb.mpmt.service.common.SerializedLambda;
import com.lzb.mpmt.service.common.WhereTreeNode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperSub<MAIN> extends MultiWrapperWhere<MAIN, MultiWrapperSub<MAIN>> {
    private List<String> selectProps;

    // List<propName opt values> 子表在主SQL下的的条件(与子表在子表sql下的where条件区分开)
    private List<WhereTreeNode> subTableWhereTrees = new ArrayList<>();

    //    mysql> SELECT * FROM table LIMIT {limitOffset},{limitSize};   //检索 第limitOffset+1行 到 limitOffset+limitSize行
    //    mysql> SELECT * FROM table LIMIT 5,10;                        //检索 第6行 到 第15行
    //    mysql> SELECT * FROM table LIMIT 95,-1;                       //检索 第96行 到 第last行.
    //    mysql> SELECT * FROM table LIMIT {limitSize};                 //检索 第1行 到 第limitSize行 (limitOffset为空)
    //    mysql> SELECT * FROM table LIMIT 5;                           //检索 第1行 到 第5行
    private Long limitOffset;
    private Long limitSize;
//    private MainTableData mainTableData = new MainTableData();

    public static <MAIN> MultiWrapperSub<MAIN> lambda(Class<MAIN> clazz) {
        return new MultiWrapperSub<>();
    }

    public <VAL> MultiWrapperSub<MAIN> select(MultiFunction<MAIN, VAL>... propFuncs) {
        selectProps = Arrays.stream(propFuncs).map(propFunc -> SerializedLambda.resolveCache(propFunc).getPropNameUnderline()).collect(Collectors.toList());
        return this;
    }

}
