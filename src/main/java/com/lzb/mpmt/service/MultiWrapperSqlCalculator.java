//package com.lzb.mpmt.service;
//
//import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
//import com.baomidou.mybatisplus.core.conditions.Wrapper;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import com.lzb.mpmt.enums.ClassRelationEnum;
//import com.lzb.mpmt.enums.JoinTypeEnum;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.experimental.SuperBuilder;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//public abstract class MultiWrapperSqlCalculator<MAIN> {
//    /**
//     * 每段SQL,例如:
//     * select * from user
//     * left join user_staff on user_staff.main_user_id = user_id and user_staff.self_state = 0
//     * where user_staff.state = 0 and user.state = 0
//     * order by user.id desc group by user.group_id
//     */
//    /**
//     * 如果有limit需要 改为 select * from (select * from user limit 0,10) user
//     */
//    private String sqlSelectFrom;
//    /**
//     * on 后面的语句 从 ClassRelationEnum 和 subTableWrapper.select(..)中读取
//     */
//    private String sqlJoin;
//    /**
//     * 从 mainTableWrapper.select(..)中读取
//     */
//    private String sqlWhere;
//    /**
//     * 从 mainTableWrapper.last().groupBy(..).orderBy()中读取
//     */
//    private String sqlLastGroupByOrderBy;
//
//
//    public static <MAIN> String calc(MultiWrapper.MainTableInfo<MAIN> mainTableInfo, List<MultiWrapper.SubTableInfo<?>> subTableInfos) {
//        MainTableData mainTableData = new MainTableData(mainTableInfo);
//        String sqlSelectFrom = calcSqlSelectFrom(mainTableInfo, subTableInfos);
////        String sqlJoin = calcSqlJoin(subTableInfos);
////        String sqlWhere = calcSqlWhere(mainTableInfo);
////        String sqlLastGroupByOrderBy = calcSqlLastGroupByOrderBy(mainTableInfo);
//        return sqlSelectFrom;
////        return sqlSelectFrom + sqlJoin + sqlWhere + sqlLastGroupByOrderBy;
//    }
//
//    private static <MAIN> String calcSqlSelectFrom(
//            MultiWrapper.MainTableInfo<MAIN> mainTableInfo,
//            List<MultiWrapper.SubTableInfo<?>> subTableInfos
//    ) {
//        String sql = "select * from user";
//        if (mainTableInfo.getLimitFlag()) {
//            Long limitOffset = mainTableInfo.getLimitOffset();
//            Long limitSize = mainTableInfo.getLimitSize();
//            sql = "select * from (" + sql + " limit " + limitOffset + "," + limitSize + ") user ";
//        }
//        return sql;
//    }
//
//
//    /**
//     * 主表各项参数
//     *
//     * @param <MAIN>
//     */
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @SuperBuilder
//    public static class MainTableData {
//        public MainTableData(MultiWrapper.MainTableInfo<?> mainTableInfo) {
//            AbstractWrapper mainTableWrapper = mainTableInfo.getMainTableWrapper();
//            String customSqlSegment = mainTableWrapper.getCustomSqlSegment();
//            System.out.println("sqlSegment=" + customSqlSegment);
//            this.tableName = StringUtils.camelToUnderline(mainTableWrapper.getEntityClass().getSimpleName());
//            this.selectProps = Arrays.stream(mainTableInfo.getMainTableWrapper().getSqlSelect().split(",")).collect(Collectors.toList());
////            this.whereTree = mainTableInfo.getLimitFlag();
////            this.subTableWhereTrees = mainTableInfo.getLimitFlag();
//
//            this.limitFlag = mainTableInfo.getLimitFlag();
//            this.limitOffset = mainTableInfo.getLimitOffset();
//            this.limitSize = mainTableInfo.getLimitSize();
//        }
//
//        private String tableName;
//        private List<String> selectProps;
//        // 多个条件 n1 and ( n2 or n3 )
//        private WhereTreeNode whereTree;
//
//        // List<propName opt values> 子表在主SQL下的的条件(与子表在子表sql下的where条件区分开)
//        private List<WhereTreeNode> subTableWhereTrees;
//        //是否要limit
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
//    public static class SubTableData {
//        private String tableName;
//        private List<String> selectProps;
//        private WhereTreeNode whereTree;
//
//        private JoinTypeEnum joinType;
//        private ClassRelationEnum classRelation;
//    }
//}
