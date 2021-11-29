package com.lzb.mpmt.service.multiwrapper.sqlsegment;


import com.lzb.mpmt.service.multiwrapper.constant.MultiConstant;

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


    default String getSqlFromLimit(String tableName) {
        if (null == getLimitSize()) {
            return tableName;
        } else {
            return "(select * from " + tableName + " limit " + valToStr(getLimitOffset(), ",") + valToStr(getLimitSize(), MultiConstant.Strings.EMPTY) + ") " + tableName;
        }
    }

    /**
     * long转字符串
     */
    default String valToStr(Long l, String appendLast) {
        return l == null ? MultiConstant.Strings.EMPTY : l + appendLast;
    }


}
