package com.lzb.mpmt.service.main;

import com.lzb.mpmt.service.common.WhereTreeNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 主表各项参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MainTableData {
    private String tableName;
    private List<String> selectProps;
    // 多个条件 n1 and ( n2 or n3 )
    private WhereTreeNode whereTree;

    // List<propName opt values> 子表在主SQL下的的条件(与子表在子表sql下的where条件区分开)
    private List<WhereTreeNode> subTableWhereTrees;

    //    mysql> SELECT * FROM table LIMIT {limitOffset},{limitSize};   //检索 第limitOffset+1行 到 limitOffset+limitSize行
    //    mysql> SELECT * FROM table LIMIT 5,10;                        //检索 第6行 到 第15行
    //    mysql> SELECT * FROM table LIMIT 95,-1;                       //检索 第96行 到 第last行.
    //    mysql> SELECT * FROM table LIMIT {limitSize};                 //检索 第1行 到 第limitSize行 (limitOffset为空)
    //    mysql> SELECT * FROM table LIMIT 5;                           //检索 第1行 到 第5行
    private Long limitOffset;
    private Long limitSize;
}