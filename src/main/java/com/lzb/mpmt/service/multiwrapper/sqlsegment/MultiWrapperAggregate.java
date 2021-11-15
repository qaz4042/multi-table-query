package com.lzb.mpmt.service.multiwrapper.sqlsegment;


import com.lzb.mpmt.service.multiwrapper.sqlsegment.aggregate.MultiAggregateInfo;
import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.SerializedLambda;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.SerializedLambdaData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@SuppressWarnings({"unchecked", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface MultiWrapperAggregate<T, Wrapper extends MultiWrapperAggregate<T, Wrapper>> {


    String getTableName();

    void setTableName(String tableName);

    List<String> getSelectFields();

    void setSelectFields(List<String> list);

    List<MultiAggregateInfo> getMultiAggregateInfos();

    default <VAL> Wrapper sum(MultiFunction<T, VAL> propFunc) {
        return sum(propFunc, null);
    }

    /***
     * 设置查询字段列表,不设置则默认*(全查询)
     *
     * @param propFunc 某个字段
     * @param <VAL> 字段泛型
     * @return 当前wrapper
     */
    default <VAL> Wrapper sum(MultiFunction<T, VAL> propFunc, String alias) {
        assert null != propFunc;
        SerializedLambdaData lambdaData = SerializedLambda.resolveCache(propFunc);
        if (null == getTableName()) {
            this.setTableName(lambdaData.getTableName());
        }

        getMultiAggregateInfos().add(
                MultiAggregateInfo.builder()
                        .aggregateType(MultiConstant.MultiAggregateTypeEnum.SUM)
//                        .relationCode()//todo 加载完把relationCode,set进去
                        .fieldName(lambdaData.getFieldName())
                        .alias(alias)
                        .build()
        );
        return (Wrapper) this;
    }


//    default Wrapper sumAll(){
//
//    }

//    sum("求和"),
//
//    avg("求平均值"),
//
//    count("计数"),//默认对主表进行计数
//
//    countDistinct("计数去重"),
//
//    max("最大值"),//默认对主表进行计数
//
//    min("最小值"),//默认对主表进行计数,
//
//    //select SId, group_concat(cId,cName),group_concat(score order by score desc separator '  ')   group_concat_max_len  如果没有group by 默认合成一条
//    group_concat("分组组合拼接"),


    default String getSqlAggregate() {
        return this.getMultiAggregateInfos().stream().map(aggregateInfo ->
                {
                    String alias = aggregateInfo.getAlias();
                    alias = null == alias ? "" : " as " + alias;
                    return aggregateInfo.getAggregateType().name() + "(" + aggregateInfo.getRelationCode() + "." + aggregateInfo.getFieldName() + ")"
                            + alias;
                }
        ).collect(Collectors.joining(","));
    }

    /**
     * long转字符串
     */
    private String valToStr(Long l, String appendLast) {
        return l == null ? MultiConstant.Strings.EMPTY : l + appendLast;
    }


}
