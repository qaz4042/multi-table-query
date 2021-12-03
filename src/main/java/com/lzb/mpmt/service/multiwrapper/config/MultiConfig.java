package com.lzb.mpmt.service.multiwrapper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzb.mpmt.service.multiwrapper.IMultiTableRelationService;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.IMultiSqlExecutor;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.MultiJdbcTemplateSqlExecutor;
import com.lzb.mpmt.service.multiwrapper.util.MultiClassRelationFactory;
import com.lzb.mpmt.service.multiwrapper.util.json.jackson.MultiEnumSerializeConfigJackson;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 基础配置
 */
@Configuration
@EnableConfigurationProperties(MultiProperties.class)
public class MultiConfig {

    /***
     * sql执行实现
     * @return IMultiSqlExecutor
     */
    @Bean
    @ConditionalOnMissingBean(IMultiSqlExecutor.class)
    public IMultiSqlExecutor multiSqlExecutor(JdbcTemplate jdbcTemplate) {
        return new MultiJdbcTemplateSqlExecutor(jdbcTemplate);
    }

    /***
     * 初始化表关系信息(方便查询时,直接缺省式使用)
     * @return IMultiSqlExecutor
     */
    @Bean
    @ConditionalOnMissingBean(MultiClassRelationFactory.class)
    public MultiClassRelationFactory multiTableRelationFactory(IMultiTableRelationService multiTableRelationService) {
        return new MultiClassRelationFactory(multiTableRelationService);
    }


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
