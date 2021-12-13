package com.bebetter.mtq.service.multiwrapper.sqlsegment;


import com.bebetter.mtq.service.multiwrapper.constant.MultiConstant;
import com.bebetter.mtq.service.multiwrapper.sqlsegment.aggregate.MultiAggregateInfo;
import com.bebetter.mtq.service.multiwrapper.util.MultiUtil;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.SerializedLambda;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.SerializedLambdaData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@SuppressWarnings({"unchecked", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface MultiWrapperAggregate<T, Wrapper extends MultiWrapperAggregate<T, Wrapper>> {


    String RELATION_CODE_TEMP_ALIAS_ALIAS = "Alias_Alias";

    String getClassName();

    void setClassName(String className);

    List<MultiAggregateInfo> getAggregateInfos();

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
        aggregate(propFunc, alias, MultiConstant.MultiAggregateTypeEnum.SUM);
        return (Wrapper) this;
    }

    default <VAL> void aggregate(MultiFunction<T, VAL> propFunc, String alias, MultiConstant.MultiAggregateTypeEnum aggregateType) {
        assert null != propFunc;
        SerializedLambdaData lambdaData = SerializedLambda.resolveCache(propFunc);
        if (null == getClassName()) {
            this.setClassName(MultiUtil.firstToLowerCase(lambdaData.getClazz().getSimpleName()));
        }

        getAggregateInfos().add(
                MultiAggregateInfo.builder()
                        .aggregateType(aggregateType)
//                        .relationCode()//关系树加载完后,把relationCode,set进去
                        .propName(lambdaData.getPropName())
                        .alias(alias)
                        .build()
        );
    }

    default List<String> getSqlAggregate(String relationCode) {
        return this.getAggregateInfos().stream().map(aggregateInfo ->
                appendOneField(aggregateInfo.getAggregateType(), relationCode, aggregateInfo.getPropName(), aggregateInfo.getAlias())
        ).collect(Collectors.toList());
    }

    static String appendOneField(MultiConstant.MultiAggregateTypeEnum aggregateAllType, String relationCode, String fieldName, String alias) {
        if (alias == null) {
            alias = aggregateAllType.name() + "." + relationCode + "." + fieldName;
        } else {
            alias = aggregateAllType.name() + "." + RELATION_CODE_TEMP_ALIAS_ALIAS + "." + alias;
        }
        return String.format(aggregateAllType.getSqlTemplate(), relationCode + "." + fieldName) + " \"" + alias + "\"";
    }
}
