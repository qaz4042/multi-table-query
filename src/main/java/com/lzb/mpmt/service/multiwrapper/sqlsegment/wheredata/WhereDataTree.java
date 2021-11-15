package com.lzb.mpmt.service.multiwrapper.sqlsegment.wheredata;

import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant.WhereAndOrEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WhereDataTree implements IWhereData {
    /**
     * 默认是and 遇到or才改为or
     */
    private WhereAndOrEnum andOr = WhereAndOrEnum.and;
    private List<IWhereData> whereDatas = new ArrayList<>(8);

    @Override
    public String getSqlWhereProps(String tableName) {
        return whereDatas.stream().map(o -> {
            String sqlWhereProps = o.getSqlWhereProps(tableName);
            if (o instanceof WhereDataTree && ((WhereDataTree) o).getWhereDatas().size() > 0) {
                sqlWhereProps = "(" + sqlWhereProps + ")";
            }
            return sqlWhereProps;
        }).collect(Collectors.joining("\n    " + andOr.name() + " "));
    }
}