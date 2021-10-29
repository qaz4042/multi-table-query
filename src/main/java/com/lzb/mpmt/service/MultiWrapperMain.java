package com.lzb.mpmt.service;

import com.lzb.mpmt.service.common.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapperMain<MAIN> extends MultiWrapperWhere<MAIN, MultiWrapperMain<MAIN>> {
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

    public static <MAIN> MultiWrapperMain<MAIN> lambda(Class<MAIN> clazz) {
        return new MultiWrapperMain<>();
    }

    public <VAL> MultiWrapperMain<MAIN> select(MultiFunction<MAIN, VAL>... propFuncs) {
        selectProps = Arrays.stream(propFuncs).map(propFunc -> SerializedLambda.resolveCache(propFunc).getPropNameUnderline()).collect(Collectors.toList());
        return this;
    }

    public <VAL> MultiWrapperMain<MAIN> limit(long offset, long size) {
        this.limitOffset = offset;
        this.limitSize = size;
        return this;
    }
}
