package com.lzb.mpmt.service.intf;


@SuppressWarnings({"unused", "unchecked"})
public interface MultiWrapperLimit<T, Wrapper extends MultiWrapperLimit<T, Wrapper>> {

//     Long limitOffset;
//     Long limitSize;

    void setLimitOffset(Long limitOffset);

    void setLimitSize(Long limitSize);

    default <VAL> Wrapper limit(long offset, long size) {
        setLimitOffset(offset);
        setLimitSize(size);
        return (Wrapper) this;
    }
}
