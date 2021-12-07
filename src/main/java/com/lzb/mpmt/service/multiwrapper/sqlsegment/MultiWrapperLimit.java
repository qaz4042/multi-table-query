package com.lzb.mpmt.service.multiwrapper.sqlsegment;


import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;
import com.lzb.mpmt.service.multiwrapper.util.MultiUtil;

/**
 * @author Administrator
 */
@SuppressWarnings({"unchecked", "AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc"})
public interface MultiWrapperLimit<T, Wrapper extends MultiWrapperLimit<T, Wrapper>> {


    /***/
    void setLimitOffset(Long limitOffset);

    Long getLimitOffset();

    void setLimitSize(Long limitSize);

    Long getLimitSize();

    default Wrapper limit(long offset, long size) {
        setLimitOffset(offset);
        setLimitSize(size);
        return (Wrapper) this;
    }


    default String getSqlFromLimit(String className) {
        if (null == getLimitSize()) {
            return MultiUtil.camelToUnderline(className) + " " + className;
        } else {
            return "(select * from " + MultiUtil.camelToUnderline(className) + " " + className + " limit " + valToStr(getLimitOffset(), ",") + valToStr(getLimitSize(), MultiConstant.Strings.EMPTY) + ") " + className;
        }
    }

    /**
     * long转字符串
     */
    default String valToStr(Long l, String appendLast) {
        return l == null ? MultiConstant.Strings.EMPTY : l + appendLast;
    }


}
