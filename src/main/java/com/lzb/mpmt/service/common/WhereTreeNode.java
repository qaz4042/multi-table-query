package com.lzb.mpmt.service.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WhereTreeNode {
    //默认是and 遇到or才改为or
    private WhereAndOrEnum andOr = WhereAndOrEnum.and;
    private List<WhereTreeNodeData> whereDatas = new ArrayList<>(8);
    private List<WhereTreeNode> subNodes = new ArrayList<>(8);
}