package com.bebetter.mtq.service.multiwrapper.constant;

import com.bebetter.mtq.service.multiwrapper.util.MultiUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

/**
 * 常量
 *
 * @author Administrator
 */
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
        private final String joinSqlSegment;
    }

    /**
     * 聚合操作
     *
     * @author Administrator
     */
    @Getter
    @AllArgsConstructor
    public enum MultiAggregateTypeEnum {
        /**
         * 聚合操作
         */
        SUM("求和", "IFNULL(SUM(%s), 0)", c -> Integer.class.isAssignableFrom(c) || BigDecimal.class.isAssignableFrom(c)),
        AVG("求平均值", "AVG(%s)", c -> Integer.class.isAssignableFrom(c) || BigDecimal.class.isAssignableFrom(c)),
        COUNT("计数", "COUNT(%s)", c -> false),
        COUNT_DISTINCT("计数去重", "COUNT(DISTINCT %s)", c -> true),
        MAX("最大值", "MAX(%s)", c -> true),
        MIN("最小值", "MIN(%s)", c -> true),

        //select SId, group_concat(cId,cName),group_concat(score order by score desc separator '  ')   group_concat_max_len  如果没有group by 默认合成一条
        GROUP_CONCAT("分组组合拼接", "GROUP_CONCAT(%s)", c -> true),
//        JSON_ARRAYAGG("组装成JsonArray"),  //JSON_ARRAYAGG(col or expr) 　　将结果集聚合为单个JSON数组，其元素由参数列的值组成。此数组中元素的顺序未定义。该函数作用于计算为单个值的列或表达式。
//        JSON_OBJECTAGG("组装成JsonObject"), //JSON_OBJECTAGG(key,value)     两个列名或表达式作为参数，第一个用作键，第二个用作值，并返回包含键值对的JSON对象。
        ;
        private final String label;
        private final String sqlTemplate;
        /**
         * 聚合函数只适配的字段,对应java类型
         */
        private final Function<Class<?>, Boolean> fieldTypeFilter;
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
        /**
         * 查询where操作类型
         */
        eq("%s = '%s'", MultiConstant.Strings.EMPTY),
        isNull("%s is null", "#nu#"),
        isNotNull("%s is not null", "#nn#"),
        in("%s in (%s)", "#in#"),
        not_in("%s not in (%s)", "#ni#"),
        gt("%s > '%s'", "#gt#"),
        ge("%s >= '%s'", "#ge#"),
        lt("%s < '%s'", "#lt#"),
        le("%s <= '%s'", "#le#"),
        //%%转义为%   张三% 才能走索引
        likeAll("%s like '%%%s%%'", "#la#"),
        likeLeft("%s like '%%%s'", "#ll#"),
        likeRight("%s like '%s%%'", "#lr#"),
        ;

        private final String template;
        private final String paramMapOptPrefix;
        public static final Map<String, WhereOptEnum> PARAM_MAP_OPT_PREFIX_MAP = MultiUtil.listToMap(Arrays.asList(WhereOptEnum.values()), WhereOptEnum::getParamMapOptPrefix);
    }
}
