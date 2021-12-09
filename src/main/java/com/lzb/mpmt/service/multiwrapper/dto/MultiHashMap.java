package com.lzb.mpmt.service.multiwrapper.dto;

import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.MultiFunction;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.SerializedLambda;
import com.lzb.mpmt.service.multiwrapper.util.mybatisplus.SerializedLambdaData;

import java.util.HashMap;

/**
 * 就是HashMap,扩展getFirstValue方法方便调用
 *
 * @param <K>
 * @param <V>
 */
public class MultiHashMap<K, V> extends HashMap<K, V> {

    public MultiHashMap() {
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
        return get(serializedLambdaData.getPropName());
    }

    private static final MultiHashMap<?, ?> EMPTY = new MultiHashMap<>();

    public static <K, V> MultiHashMap<K, V> emptyMap() {
        //noinspection unchecked
        return (MultiHashMap<K, V>) EMPTY;
    }
}
