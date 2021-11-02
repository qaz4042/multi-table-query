package com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata;

import com.lzb.mpmt.service.multiwrapper.enums.WhereOptEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WhereDataUnit implements IWhereData {
    private String propName;
    private WhereOptEnum opt;
    private Object values;


    @Override
    public String getSqlWhereProps(String tableName) {
        return opt.getSqlFunction().apply(tableName + "." + propName, values);
    }
}
