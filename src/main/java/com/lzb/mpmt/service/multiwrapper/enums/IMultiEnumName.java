package com.lzb.mpmt.service.multiwrapper.enums;

import java.io.Serializable;

/**
 * 枚举实现
 * <VALUE> 必须是可以数据库透传的类型,比如int string等等
 * @author Administrator
 */
public interface IMultiEnumName extends IMultiEnum<String> {

    /**
     * value
     *
     * @return value
     */
    String name();

    /**
     * 中文名称
     *
     * @return  中文名称
     */
    @Override
    default String getValue() {
        return name();
    }
}
