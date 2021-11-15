package com.lzb.mpmt.service.multiwrapper.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class MultiConstant {

    /***
     * 公共常量 日期相关
     */
    public static class Strings {
        public static final String EMPTY = "";
        public static final char UNDERLINE = '_';
        public static final String ID_FIELD_NAME_DEFAULT = "id";
    }

    /***
     * 公共常量 日期相关
     */
    public static class DateTimes {
        public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    }

    /**
     * multi框架相关常量
     */
    public static class MultiStrings {
//        public static final String ID_FIELD_NAME = "";
    }


    /**
     * 类和类的关系  可以自动生成left join 统一规范sql
     *
     * @author Administrator
     */
    @AllArgsConstructor
    @Getter
    public enum ClassRelationOneOrManyEnum {
        /***/
        ONE("在关系里为一对多/一对一里面的一"),
        MANY("在关系里为一对多/多对多里面的多"),
        ;
        private final String label;
    }

    /**
     * @author Administrator
     */
    @Getter
    @AllArgsConstructor
    public enum JoinTypeEnum {
        /***/
        left_join("left join "),
        inner_join("inner join "),
        right_join("right join "),
        ;
        private final String sql;
    }

    /**
     * 聚合操作
     *
     * @author Administrator
     */
    @Getter
    @AllArgsConstructor
    public enum MultiAggregateTypeEnum {
        SUM("求和"),
        AVG("求平均值"),
        COUNT("计数"),//默认对主表进行计数
        COUNT_DISTINCT("计数去重"),
        MAX("最大值"),//默认对主表进行计数
        MIN("最小值"),//默认对主表进行计数,
        //select SId, group_concat(cId,cName),group_concat(score order by score desc separator '  ')   group_concat_max_len  如果没有group by 默认合成一条
        GROUP_CONCAT("分组组合拼接"),
//        JSON_ARRAYAGG("组装成JsonArray"),  //JSON_ARRAYAGG(col or expr) 　　将结果集聚合为单个JSON数组，其元素由参数列的值组成。此数组中元素的顺序未定义。该函数作用于计算为单个值的列或表达式。
//        JSON_OBJECTAGG("组装成JsonObject"), //JSON_OBJECTAGG(key,value)     两个列名或表达式作为参数，第一个用作键，第二个用作值，并返回包含键值对的JSON对象。
        ;
        private final String label;
    }


    /**
     * @author Administrator
     */
    @AllArgsConstructor
    @Getter
    public enum PageOrderEnum {
        /**
         * 正序
         */
        asc,
        /**
         * 倒序
         */
        desc,
        ;
    }

    /**
     * @author Administrator
     */
    @Getter
    @AllArgsConstructor
    public enum WhereAndOrEnum {
        /***/
        and,
        or,
    }


    /**
     * @author Administrator
     */
    @Getter
    @AllArgsConstructor
    public enum WhereOptEnum {
        /***/
        eq("%s = '%s'", "age = 18"),
        isNull("%s is null", "age is null"),
        isNotNull("%s is not null", "age is not null"),
        in("%s in (%s)", "age in (18,19)"),
        not_in("%s not in (%s)", "age not in (18,19)"),
        gt("%s > '%s'", "age > '18'"),
        ge("%s >= '%s'", "age >= '18'"),
        lt("%s < '%s'", "age < '18'"),
        le("%s <= '%s'", "age <= '18'"),
        //%%转义为%
        likeDefault("%s like '%%%s%%'", "name like '%咔咔%'"),
        ;

        private final String template;
        private final String demo;
    }
}
