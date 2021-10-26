//package com.lzb.mpmt.service;
//
//import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
//import com.baomidou.mybatisplus.core.conditions.Wrapper;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.lzb.mpmt.enums.ClassRelationEnum;
//import com.lzb.mpmt.enums.JoinTypeEnum;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.experimental.SuperBuilder;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Data
//@NoArgsConstructor
//@SuppressWarnings("unused")
//public class MultiWrapperSub<MAIN> {
//
//    //主表信息
//    private MainTableInfo<MAIN> mainTableInfo = new MainTableInfo<>();
//
//    //副表信息
//    private List<SubTableInfo<?>> subTableInfos = new ArrayList<>(8);
//
//    /**
//     * 例如
//     * select * from user
//     * left join user_staff on user_staff.main_user_id = user_id and user_staff.self_state = 0
//     * where user_staff.state = 0 and user.state = 0
//     * 主表的过滤条件 相当于例子中的 user_staff.state = 0
//     * 副表的过滤条件 相当于例子中的 user_staff.self_state = 0
//     *
//     * @return MultiWrapper
//     */
//    public static <MAIN> MultiWrapperSub<MAIN> main(AbstractWrapper<MAIN, ?, ?> mainTableWrapper, Wrapper<?>... subTableMainWhere) {
//        MultiWrapperSub<MAIN> wrapper = new MultiWrapperSub<>();
//        wrapper.getMainTableInfo().setMainTableWrapper(mainTableWrapper);
//        wrapper.getMainTableInfo().setSubTableMainWhere(subTableMainWhere);
//        return wrapper;
//    }
//
//    /***
//     * join是有顺序的,前后两张表,必须有直接关联
//     * @param subTableWrapper subTableWrapper
//     * @return MultiWrapper
//     */
//    public <SUB> MultiWrapperSub<MAIN> leftJoin(Wrapper<SUB> subTableWrapper) {
//        return leftJoin(null, subTableWrapper);
//    }
//
//    /***
//     * join是有顺序的,前后两张表,必须有直接关联
//     * @param subTableWrapper subTableWrapper
//     * @return MultiWrapper
//     */
//    public <SUB> MultiWrapperSub<MAIN> innerJoin(Wrapper<SUB> subTableWrapper) {
//        return innerJoin(null, subTableWrapper);
//    }
//
//    /***
//     * 分页信息
//     * @param page 分页信息
//     * @return MultiWrapper
//     */
//    public MultiWrapperSub<MAIN> limit(Page<MAIN> page) {
//        return limit(page.offset(), page.getSize());
//    }
//
//    /***
//     * 分页信息
//     * @param offset 数据起始index(0开始)
//     * @param size   数据最大条数
//     * @return MultiWrapper
//     */
//    public MultiWrapperSub<MAIN> limit(long offset, long size) {
////        LIMIT #{offset}, #{pageSize}
//        mainTableInfo.setLimitFlag(true);
//        mainTableInfo.setLimitSize(size);
//        mainTableInfo.setLimitOffset(offset);
//        return this;
//    }
//
//    /***
//     * join是有顺序的,前后两张表,必须有直接关联
//     * @param subTableWrapper subTableWrapper
//     * @return MultiWrapper
//     */
//    public <SUB> MultiWrapperSub<MAIN> leftJoin(ClassRelationEnum relationEnum, Wrapper<SUB> subTableWrapper) {
//        JoinTypeEnum joinType = JoinTypeEnum.left_join;
//        return this.getMainMultiWrapper(relationEnum, subTableWrapper, joinType);
//    }
//
//    public <SUB> MultiWrapperSub<MAIN> innerJoin(ClassRelationEnum relationEnum, Wrapper<SUB> subTableWrapper) {
//        JoinTypeEnum joinType = JoinTypeEnum.inner_join;
//        return this.getMainMultiWrapper(relationEnum, subTableWrapper, joinType);
//    }
//
//    private <SUB> MultiWrapperSub<MAIN> getMainMultiWrapper(ClassRelationEnum relationEnum, Wrapper<SUB> subTableWrapper, JoinTypeEnum joinType) {
//        subTableInfos.add(
//                SubTableInfo.<SUB>builder()
//                        .wrapper(subTableWrapper)
//                        .joinType(joinType)
//                        .classRelation(relationEnum)
//                        .build()
//        );
//        return this;
//    }
//
//
//    /***
//     * 参数可以统一格式map传递(容易缺失编译约束,不建议后端自查询频繁使用)
//     * @param allTableParamMap allTableParamMap
//     * @return MultiWrapper
//     */
//    public MultiWrapperSub<MAIN> allTableParamMap(Map<String, Object> allTableParamMap) {
//        //可能还有分页的limit 按主表去limit
//        return this;
//    }
//
//    //输出最终sql
//    public String computeSql() {
//        return MultiWrapperSqlCalculator.calc(mainTableInfo, subTableInfos);
//    }
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @SuperBuilder
//    public static class MainTableInfo<MAIN> {
//        //主表各项参数
//        private AbstractWrapper mainTableWrapper;
//        //副表主表数据过滤参数
//        private Wrapper<?>[] subTableMainWhere;
//        //是否分页
//        private Boolean limitFlag;
//        //要查出的数据开始序号
//        private Long limitOffset;
//        //要查出的数据行数
//        private Long limitSize;
//    }
//
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
//
//}
