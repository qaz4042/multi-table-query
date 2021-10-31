package com.lzb.mpmt.service.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WhereTreeNode implements IWhereTreeData {
    //默认是and 遇到or才改为or
    private WhereAndOrEnum andOr = WhereAndOrEnum.and;
    private List<IWhereTreeData> whereTreeDatas = new ArrayList<>(8);

    @Override
    public String toSql(String tableName) {
        return "(" + whereTreeDatas.stream().map(o -> o.toSql(tableName)).collect(Collectors.joining(") " + andOr.name() + " (")) + ")";
    }
}