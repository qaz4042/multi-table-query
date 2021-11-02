package com.lzb.mpmt.service.intf;


import com.lzb.mpmt.service.util.MutilUtil;

@SuppressWarnings({"unused", "unchecked"})
public interface MultiWrapperLimit<T, Wrapper extends MultiWrapperLimit<T, Wrapper>> {

//     Long limitOffset;

    //     Long limitSize;
    void setLimitOffset(Long limitOffset);

    Long getLimitOffset();

    void setLimitSize(Long limitSize);

    Long getLimitSize();

    default <VAL> Wrapper limit(long offset, long size) {
        setLimitOffset(offset);
        setLimitSize(size);
        return (Wrapper) this;
    }

    default String getSqlFromLimit(String tableName) {
        if (null == getLimitSize()) {
            return tableName;
        } else {
            return "(select * from " + tableName + " limit " + valToStr(getLimitOffset(), ",") + valToStr(getLimitOffset(), MutilUtil.EMPTY) + ")";
        }
    }

    /**
     * long转字符串
     */
    private String valToStr(Long l, String appendLast) {
        return l == null ? MutilUtil.EMPTY : l + appendLast;
    }


}
