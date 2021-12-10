package com.lzb.mpmt.service.multiwrapper.config;

import com.lzb.mpmt.service.multiwrapper.executor.MultiExecutorInner;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.MultiDbAdaptor;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.MultiDbJdbcAdaptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DB数据库配置 暂时支持
 *
 * @author Administrator
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(MultiDbAdaptor.class)
@ConditionalOnBean(MultiDbJdbcAdaptor.class)
public class MultiConfigDbJdbc {

    /***
     * sql执行实现
     * @return IMultiSqlExecutor
     */
    @Bean
    @ConditionalOnMissingBean(MultiDbAdaptor.class)
    public MultiDbAdaptor multiSqlExecutor(MultiDbJdbcAdaptor executor, MultiProperties multiProperties) {
        MultiExecutorInner.executor = executor;
        MultiExecutorInner.multiProperties = multiProperties;
        return executor;
    }
}
