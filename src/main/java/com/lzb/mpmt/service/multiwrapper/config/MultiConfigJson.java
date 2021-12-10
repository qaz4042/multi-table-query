package com.lzb.mpmt.service.multiwrapper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzb.mpmt.service.multiwrapper.MultiTableRelationService;
import com.lzb.mpmt.service.multiwrapper.executor.MultiExecutorInner;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.MultiJdbcJdbcSpringSqlExecutor;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.MultiSqlExecutorIntf;
import com.lzb.mpmt.service.multiwrapper.util.MultiClassRelationFactory;
import com.lzb.mpmt.service.multiwrapper.util.json.jackson.MultiEnumSerializeConfigJackson;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 基础配置
 *
 * @author Administrator
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ObjectMapper.class)
public class MultiConfigJson {

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
