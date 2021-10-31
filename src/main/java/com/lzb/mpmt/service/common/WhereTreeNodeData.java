package com.lzb.mpmt.service.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WhereTreeNodeData implements IWhereTreeData {
    private String propName;
    private WhereOptEnum opt;
    private Object values;


    @Override
    public String toSql(String tableName) {
        return opt.getSqlFunction().apply(tableName + "." + propName, values);
    }
}
