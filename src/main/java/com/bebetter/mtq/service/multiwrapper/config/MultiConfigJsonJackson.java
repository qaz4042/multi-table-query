package com.bebetter.mtq.service.multiwrapper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bebetter.mtq.service.multiwrapper.util.json.jackson.MultiEnumSerializeConfigJackson;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JSON框架 数据库配置 (为了支持,枚举在java对象中为枚举,在数据和请求中都为Integer/String类型)
 *
 * @author Administrator
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ObjectMapper.class)
public class MultiConfigJsonJackson {

    /**
     * 实体类,转json和入库,自动转为自定义枚举
     *
     * @param objectMapper 扩展jackson配置(暂时只有jackson支持定制(接口-子类)的序列化)
     */
    @Bean
    @ConditionalOnBean(ObjectMapper.class)
    public ObjectMapper objectMapperAddConfigs(ObjectMapper objectMapper) {
        //实体类可以直接放置 实现了IMultiEnum的枚举
        MultiEnumSerializeConfigJackson.addEnumAndNotNullConfigs(objectMapper);
        return objectMapper;
    }
}
