package com.bebetter.mtq.service.multiwrapper.sqlsegment;


import com.bebetter.mtq.service.multiwrapper.constant.MultiConstant;
import com.bebetter.mtq.service.multiwrapper.util.MultiTuple2;
import com.bebetter.mtq.service.multiwrapper.util.MultiUtil;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.MultiFunction;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
@SuppressWarnings({"unchecked", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface MultiWrapperLimit<T, Wrapper extends MultiWrapperLimit<T, Wrapper>> {

    String getClassName();

    void setClassName(String className);

    /***/
    void setLimitOffset(Long limitOffset);

    Long getLimitOffset();

    void setLimitSize(Long limitSize);

    Long getLimitSize();

    void setOrderInfos(List<String> orderInfos);

    List<String> getOrderInfos();

    default Wrapper limit(long offset, long size) {
        setLimitOffset(offset);
        setLimitSize(size);
        return (Wrapper) this;
    }

    default Wrapper limit(long offset, long size, List<String> orderInfos) {
        setLimitOffset(offset);
        setLimitSize(size);
        setOrderInfos(orderInfos.stream().map(MultiUtil::camelToUnderline).collect(Collectors.toList()));
        return (Wrapper) this;
    }

    default <VAL> Wrapper asc(MultiFunction<T, VAL>... propFuncs) {
        this.fillOrder(" asc", propFuncs);
        return (Wrapper) this;
    }

    default <VAL> Wrapper desc(MultiFunction<T, VAL>... propFuncs) {
        this.fillOrder(" desc", propFuncs);
        return (Wrapper) this;
    }


    default <VAL> void fillOrder(String orderOrder, MultiFunction<T, VAL>[] propFuncs) {
        MultiTuple2<String, List<String>> result = MultiUtil.calcMultiFunctions(d -> MultiUtil.camelToUnderline(d.getPropName()) + orderOrder, propFuncs);
        if (null == getClassName()) {
            this.setClassName(result.getT1());
        }
        this.setOrderInfos(result.getT2());
    }

    default String getSqlFromLimit(String className) {
        String orderByMain = String.join(",", getOrderInfos());
        orderByMain = orderByMain.length() == 0 ? orderByMain : " order by " + orderByMain;
        String mainInfo = MultiUtil.camelToUnderline(className) + " " + className + orderByMain;
        mainInfo = null == getLimitSize() ? mainInfo : "(select * from " + mainInfo + " limit " + valToStr(getLimitOffset(), ",") + valToStr(getLimitSize(), MultiConstant.Strings.EMPTY) + ") " + className;
        return mainInfo;
    }

    /**
     * long转字符串
     */
    default String valToStr(Long l, String appendLast) {
        return l == null ? MultiConstant.Strings.EMPTY : l + appendLast;
    }


}
