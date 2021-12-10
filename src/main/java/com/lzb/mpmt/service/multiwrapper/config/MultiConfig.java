package com.lzb.mpmt.service.multiwrapper.config;

import com.lzb.mpmt.service.multiwrapper.MultiTableRelationService;
import com.lzb.mpmt.service.multiwrapper.util.MultiClassRelationFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
     * 初始化表关系信息(方便查询时,直接缺省式使用)
     * @return IMultiSqlExecutor
     */
    @Bean
    @ConditionalOnMissingBean(MultiClassRelationFactory.class)
    public MultiClassRelationFactory multiTableRelationFactory() {
        return new MultiClassRelationFactory(multiTableRelationService);
    }
}
