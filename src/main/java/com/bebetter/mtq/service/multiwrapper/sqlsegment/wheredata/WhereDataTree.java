package com.bebetter.mtq.service.multiwrapper.sqlsegment.wheredata;

import com.bebetter.mtq.service.multiwrapper.constant.MultiConstant;
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
    private MultiConstant.WhereAndOrEnum andOr = MultiConstant.WhereAndOrEnum.and;
    private List<IWhereData> whereDatas = new ArrayList<>(8);

    @Override
    public String getSqlWhereProps(String className) {
        return whereDatas.stream().map(o -> {
            String sqlWhereProps = o.getSqlWhereProps(className);
            if (o instanceof WhereDataTree && ((WhereDataTree) o).getWhereDatas().size() > 0) {
                sqlWhereProps = "(" + sqlWhereProps + ")";
            }
            return sqlWhereProps;
        }).collect(Collectors.joining("\n    " + andOr.name() + " "));
    }
}