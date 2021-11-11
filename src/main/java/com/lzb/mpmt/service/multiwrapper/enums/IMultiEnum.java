package com.lzb.mpmt.service.multiwrapper.enums;

import java.io.Serializable;

/**
 * 枚举实现
 * <VALUE> 必须是可以数据库透传的类型,比如int string等等
 * @author Administrator
 */
public interface IMultiEnum<VALUE extends Serializable> {
    /**
     * 数据库存储的值
     *
     * @return 数据库
     */
    VALUE getValue();

    /**
     * 中文名称
     *
     * @return 中文名称
     */
    String getLabel();
}
