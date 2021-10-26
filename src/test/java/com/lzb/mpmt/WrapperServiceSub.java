package com.lzb.mpmt;

import com.lzb.mpmt.model.BaseModel;

import java.util.function.Function;

public class WrapperServiceSub<T> extends WrapperService<Function<T, ?>, T> {


//    @Override
    public WrapperServiceSub<T> testtest2(Function<T, ?> t) {
        return this;
    }
}
