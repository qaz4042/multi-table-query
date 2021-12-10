package com.lzb.mpmt.service.multiwrapper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzb.mpmt.service.multiwrapper.MultiTableRelationService;
import com.lzb.mpmt.service.multiwrapper.executor.MultiExecutorInner;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.MultiSqlExecutorIntf;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.MultiJdbcJdbcSpringSqlExecutor;
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
@EnableConfigurationProperties(MultiProperties.class)
public class MultiConfig {
    private final MultiTableRelationService multiTableRelationService;

    public MultiConfig(ObjectProvider<MultiTableRelationService> interceptorsProvider) {
        multiTableRelationService = interceptorsProvider.getIfAvailable();
    }

    /***
     * sql执行实现
     * @return IMultiSqlExecutor
     */
    @Bean
    @ConditionalOnMissingBean(MultiSqlExecutorIntf.class)
    public MultiSqlExecutorIntf multiSqlExecutor(JdbcTemplate jdbcTemplate, MultiProperties multiProperties) {
        MultiJdbcJdbcSpringSqlExecutor executor = new MultiJdbcJdbcSpringSqlExecutor(jdbcTemplate);
        MultiExecutorInner.executor = executor;
        MultiExecutorInner.multiProperties = multiProperties;
        return executor;
    }

    /***
     * 初始化表关系信息(方便查询时,直接缺省式使用)
     * @return IMultiSqlExecutor
     */
    @Bean
    @ConditionalOnMissingBean(MultiClassRelationFactory.class)
    public MultiClassRelationFactory multiTableRelationFactory() {
        return new MultiClassRelationFactory(multiTableRelationService);
    }
}
