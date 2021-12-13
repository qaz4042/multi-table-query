package com.bebetter.mtq.service.multiwrapper.config;

import com.bebetter.mtq.service.multiwrapper.MultiTableRelationService;
import com.bebetter.mtq.service.multiwrapper.executor.sqlexecutor.MultiDbAdaptor;
import com.bebetter.mtq.service.multiwrapper.util.MultiClassRelationFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * 基础配置
 *
 * @author Administrator
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MultiProperties.class)
public class MultiConfig {
    public static MultiProperties multiProperties;
    public static MultiClassRelationFactory multiClassRelationFactory;
    public static MultiDbAdaptor multiDbAdaptor;

    public MultiConfig(MultiProperties multiProperties,
                       ObjectProvider<MultiTableRelationService> multiTableRelationServiceProvider,
                       ObjectProvider<MultiDbAdaptor> multiDbAdaptorProvider
    ) {
        MultiConfig.multiProperties = multiProperties;
        //初始化表关系信息(方便查询时,直接缺省式使用)
        MultiConfig.multiClassRelationFactory = new MultiClassRelationFactory(Objects.requireNonNull(multiTableRelationServiceProvider.getIfAvailable()));
        //sql执行器
        MultiConfig.multiDbAdaptor = Objects.requireNonNull(multiDbAdaptorProvider.getIfAvailable());
    }

    public static void build(MultiProperties multiProperties,
                             MultiTableRelationService multiTableRelationService,
                             MultiDbAdaptor multiDbAdaptor
    ) {
        MultiConfig.multiProperties = multiProperties;
        MultiConfig.multiClassRelationFactory = new MultiClassRelationFactory(multiTableRelationService);
        MultiConfig.multiDbAdaptor = Objects.requireNonNull(multiDbAdaptor);
    }
}
