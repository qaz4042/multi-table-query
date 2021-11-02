package com.lzb.mpmt.service;

import com.lzb.mpmt.service.multiwrapper.enums.JoinTypeEnum;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.MultiWrapperWhere;
import com.lzb.mpmt.service.multiwrapper.sqlsegment.joindata.TableRelation;
import com.lzb.mpmt.service.multiwrapper.util.MutilUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 多表联查器
 * 举例说明 主表:user_staff 副表:user_staff_address
 *
 * @param <MAIN>
 * @author Administrator
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapper<MAIN> {

    /**
     * 主表信息
     */
    private MultiWrapperMain<MAIN> wrapperMain;
    private List<MultiWrapperMainSubWhere<?>> wrapperMainSubWheres = Collections.emptyList();

    /**
     * 副表信息
     */
    private List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations = new ArrayList<>(8);

    public MultiWrapper(MultiWrapperMain<MAIN> wrapperMain, MultiWrapperSub<?>... subTableWrappers) {
        this.wrapperMain = wrapperMain;
        //默认leftJoin
        Arrays.stream(subTableWrappers).forEach(this::leftJoin);
    }

    /**
     * 主表信息
     * 例如 select * from user_staff
     *
     * @return MultiWrapper
     */
    public static <MAIN> MultiWrapper<MAIN> main(MultiWrapperMain<MAIN> wrapperMain) {
        return main(wrapperMain, (MultiWrapperMainSubWhere<?>[]) null);
    }

    /**
     * 主表信息
     * 例如 select * from user_staff u
     * left join user_staff_address a on a.staff_id = u.id
     * where user_staff_address.del_flag = 0
     *
     * @return MultiWrapper
     */
    public static <MAIN> MultiWrapper<MAIN> main(MultiWrapperMain<MAIN> wrapperMain, MultiWrapperMainSubWhere<?>... wrapperMainSubWhere) {
        MultiWrapper<MAIN> wrapper = new MultiWrapper<>();
        wrapper.setWrapperMain(wrapperMain);
        if (wrapperMainSubWhere != null) {
            wrapper.setWrapperMainSubWheres(Arrays.stream(wrapperMainSubWhere).filter(Objects::nonNull).collect(Collectors.toList()));
        }
        return wrapper;
    }

    /***
     * join 副表信息
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public <SUB> MultiWrapper<MAIN> leftJoin(MultiWrapperSub<SUB> subTableWrapper) {
        return leftJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public <SUB> MultiWrapper<MAIN> innerJoin(MultiWrapperSub<SUB> subTableWrapper) {
        return innerJoin(null, subTableWrapper);
    }

    /***
     * join是有顺序的,前后两张表,必须有直接关联
     *
     * @param relationId      {@link TableRelation#getId()}
     * @param subTableWrapper 副表的select和 on内条件信息
     * @return MultiWrapper
     */
    public <SUB> MultiWrapper<MAIN> leftJoin(Long relationId, MultiWrapperSub<SUB> subTableWrapper) {
        JoinTypeEnum joinType = JoinTypeEnum.left_join;
        return this.getMainMultiWrapper(joinType, relationId, subTableWrapper);
    }

    public <SUB> MultiWrapper<MAIN> innerJoin(Long relationId, MultiWrapperSub<SUB> subTableWrapper) {
        JoinTypeEnum joinType = JoinTypeEnum.inner_join;
        return this.getMainMultiWrapper(joinType, relationId, subTableWrapper);
    }

    private <SUB> MultiWrapper<MAIN> getMainMultiWrapper(JoinTypeEnum joinType, Long relationId, MultiWrapperSub<SUB> subTableWrapper) {
        wrapperSubAndRelations.add(new MultiWrapperSubAndRelation<>(joinType, relationId, subTableWrapper));
        return this;
    }


    /***
     * 参数可以统一格式map传递(容易缺失编译约束,不建议后端自查询频繁使用)
     * @param allTableParamMap allTableParamMap
     * @return MultiWrapper
     */
    public MultiWrapper<MAIN> allTableParamMap(Map<String, Object> allTableParamMap) {
        //可能还有分页的limit 按主表去limit
        return this;
    }

    /**
     * 输出最终sql
     */
    public String computeSql() {
        String mainTableName = wrapperMain.getTableName();
        if (mainTableName == null) {
            throw new RuntimeException("请先通过MultiWrapperMain.lambda(UserInfo.class)或者.eq(UserInfo::getId)确定表名,在执行查询");
        }
        // 1. select user_staff.* from user_staff
        List<String> selectPropsList = wrapperSubAndRelations.stream().map(o -> o.getWrapperSub().getSqlSelectProps()).collect(Collectors.toList());
        selectPropsList.add(0, wrapperMain.getSqlSelectProps());
        String sqlSelect = "\nselect\n" + String.join(",\n", selectPropsList);

        // 2. 添加limit
        //	SELECT u.*,p.* FROM user_info                          u LEFT JOIN principal_user p ON p.user_id = u.id where p.admin_flag = 1;
        //	SELECT u.*,p.* FROM (select * from user_info limit 10) u LEFT JOIN principal_user p ON p.user_id = u.id where p.admin_flag = 1;
        String sqlFromLimit = "\nFROM " + wrapperMain.getSqlFromLimit(mainTableName);

        // 3. left join user_staff_address on user_staff.id = user_staff_address.staff_id
        String sqlLeftJoinOn = "\n" + wrapperSubAndRelations.stream().map(r -> r.getSqlJoin(mainTableName)).collect(Collectors.joining("\n"));

        // 4. where user_staff.state = 0
        //      and user_staff_address.del_flag = 0
        List<MultiWrapperWhere<?, ?>> whereWrappers = new ArrayList<>(wrapperMainSubWheres);
        whereWrappers.add(0, wrapperMain);
        String wherePropsAppend = whereWrappers.stream().map(MultiWrapperWhere::getSqlWhereProps).filter(s -> !MutilUtil.isEmpty(s)).collect(Collectors.joining("\n  and "));
        String sqlWhere = MutilUtil.isEmpty(wherePropsAppend) ? MutilUtil.EMPTY : "\nwhere 1=1\n  and" + wherePropsAppend;

        return sqlSelect + sqlFromLimit + sqlLeftJoinOn + sqlWhere;
    }
}
