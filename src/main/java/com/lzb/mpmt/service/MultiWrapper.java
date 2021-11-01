package com.lzb.mpmt.service;

import com.lzb.mpmt.enums.ClassRelationEnum;
import com.lzb.mpmt.enums.JoinTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * 多表联查器
 * 举例说明 主表:user_staff 副表:user_staff_address
 *
 * @param <MAIN>
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unused")
public class MultiWrapper<MAIN> {

    //主表信息
    private MultiWrapperMain<MAIN> wrapperMain;
    private List<MultiWrapperMainSubWhere<?>> wrapperMainSubWheres = Collections.emptyList();

    //副表信息
    private List<MultiWrapperSubAndRelation<?>> wrapperSubAndRelations = Collections.emptyList();

    /**
     * 主表信息
     * 例如 select * from user_staff
     *
     * @return MultiWrapper
     */
    public static <MAIN> MultiWrapper<MAIN> main(MultiWrapperMain<MAIN> wrapperMain) {
        return main(wrapperMain, null);
    }

    /**
     * 主表信息
     * 例如 select * from user_staff u
     * left join user_staff_address a on a.staff_id = u.id
     * where user_staff_address.del_flag = 0
     *
     * @return MultiWrapper
     */
    public static <MAIN> MultiWrapper<MAIN> main(MultiWrapperMain<MAIN> wrapperMain, List<MultiWrapperMainSubWhere<?>> wrapperMainSubWhere) {
        MultiWrapper<MAIN> wrapper = new MultiWrapper<>();
        wrapper.setWrapperMain(wrapperMain);
        wrapper.setWrapperMainSubWheres(wrapperMainSubWhere);
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
     * @param subTableWrapper subTableWrapper
     * @return MultiWrapper
     */
    public <SUB> MultiWrapper<MAIN> leftJoin(ClassRelationEnum relationEnum, MultiWrapperSub<SUB> subTableWrapper) {
        JoinTypeEnum joinType = JoinTypeEnum.left_join;
        return this.getMainMultiWrapper(joinType, relationEnum, subTableWrapper);
    }

    public <SUB> MultiWrapper<MAIN> innerJoin(ClassRelationEnum relationEnum, MultiWrapperSub<SUB> subTableWrapper) {
        JoinTypeEnum joinType = JoinTypeEnum.inner_join;
        return this.getMainMultiWrapper(joinType, relationEnum, subTableWrapper);
    }

    private <SUB> MultiWrapper<MAIN> getMainMultiWrapper(JoinTypeEnum joinType, ClassRelationEnum relationEnum, MultiWrapperSub<SUB> subTableWrapper) {
        wrapperSubAndRelations.add(new MultiWrapperSubAndRelation<>(joinType, relationEnum, subTableWrapper));
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

    //输出最终sql
    public String computeSql() {

        // 1. select user_staff.* from user_staff

        String sqlSelect = "select " + wrapperMain.getSqlSelectProps() + " , " + wrapperSubAndRelations.stream().map(o -> o.getWrapperSub().getSqlSelectProps());

        // 2. 添加limit
        //	SELECT u.*,p.* FROM
        //	user_info u
        //	LEFT JOIN principal_user p ON p.user_id = u.id
        //	where p.admin_flag = 1;
        //	-->
        //	SELECT * FROM
        //	(select * from user_info limit 10) u
        //	LEFT JOIN principal_user p ON p.user_id = u.id
        //	where p.admin_flag = 1;
        String sqlFromLimit = wrapperMain.getSqlWhereLimit(wrapperMain.getTableName());

        // 3. left join user_staff_address on user_staff.id = user_staff_address.staff_id
        String sqlLeftJoinOn = wrapperSubAndRelations.stream();

        // 4. where user_staff.state = 0
        //      and user_staff_address.del_flag = 0
        String sqlWhere = " where " + wrapperMain.getSqlWhere(wrapperMainSubWheres);


        return sqlSelect + sqlFromLimit + sqlLeftJoinOn + sqlWhere
    }


//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @SuperBuilder
//    public static class SubTableInfo<SUB> {
//
//        private Wrapper<SUB> wrapper;
//        private JoinTypeEnum joinType;
//        private ClassRelationEnum classRelation;
//
//        public SubTableInfo(Wrapper<SUB> wrapper) {
//            LambdaQueryWrapper<Object> objectLambdaQueryWrapper = Wrappers.lambdaQuery();
//        }
//    }

}
