package com.lzb.mpmt.service.multiwrapper.dto;

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

    private static final MultiHashMap<?, ?> EMPTY = new MultiHashMap<>();

    public static <K, V> MultiHashMap<K, V> emptyMap() {
        //noinspection unchecked
        return (MultiHashMap<K, V>) EMPTY;
    }
}
