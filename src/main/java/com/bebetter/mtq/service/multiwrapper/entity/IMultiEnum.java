package com.bebetter.mtq.service.multiwrapper.entity;

import java.io.Serializable;

/**
 * 枚举实现 取枚举的value属性入库
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
