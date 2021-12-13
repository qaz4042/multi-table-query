package com.bebetter.mtq.service.multiwrapper.util.mybatisplus;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author Administrator
 */
@FunctionalInterface
public interface MultiFunction<T, R> extends Function<T, R>, Serializable {
}
