package com.bebetter.mtq.service.multiwrapper.dto;

import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.SerializedLambda;
import com.bebetter.mtq.service.multiwrapper.util.mybatisplus.SerializedLambdaData;

import java.util.HashMap;

/**
 * HashMap,扩展getValue框架便捷方法
 * @author Administrator
 * @param <K>
 * @param <V>
 */
public class MultiHashMap<K, V> extends HashMap<K, V> {

    public MultiHashMap() {
        super();
    }

    public MultiHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 添加getFirst方法
     *
     * @return 方便快速取值
     */
    public V getFirstValue() {
        return this.values().size() > 0 ? this.values().iterator().next() : null;
    }

    /**
     * 添加getFirst方法
     *
     * @return 方便快速取值
     */
    public <T, VAL> V getValue(String relationCode, MultiFunction<T, VAL> prop) {
        SerializedLambdaData serializedLambdaData = SerializedLambda.resolveCache(prop);
        return get(relationCode + "." + serializedLambdaData.getPropName());
    }

    private static final MultiHashMap<?, ?> EMPTY = new MultiHashMap<>();

    public static <K, V> MultiHashMap<K, V> emptyMap() {
        //noinspection unchecked
        return (MultiHashMap<K, V>) EMPTY;
    }
}
