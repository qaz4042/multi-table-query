package com.bebetter.mtq.service.multiwrapper.annotations;

import com.bebetter.mtq.service.multiwrapper.constant.MultiConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段注释
 *
 * @author Administrator
 */
@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MultiTableField {
    /***
     * 字段中文名
     */
    String value() default MultiConstant.Strings.EMPTY;

    /***
     * 字段是否在数据库中存在
     */
    boolean exist() default true;
}
