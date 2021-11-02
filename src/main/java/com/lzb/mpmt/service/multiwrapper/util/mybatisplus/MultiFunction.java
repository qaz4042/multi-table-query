package com.lzb.mpmt.service.multiwrapper.util.mybatisplus;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface MultiFunction<T, R> extends Function<T, R>, Serializable {
}
