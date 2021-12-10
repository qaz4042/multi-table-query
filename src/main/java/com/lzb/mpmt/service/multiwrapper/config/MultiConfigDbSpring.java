package com.lzb.mpmt.service.multiwrapper.config;

import com.lzb.mpmt.service.multiwrapper.executor.MultiExecutorInner;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.MultiDbSpringAdaptor;
import com.lzb.mpmt.service.multiwrapper.executor.sqlexecutor.MultiDbAdaptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * DB数据库配置 支持spring的JdbcTemplate
 *
 * @author Administrator
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(JdbcTemplate.class)
public class MultiConfigDbSpring {

    /***
     * sql执行实现
     * @return IMultiSqlExecutor
     */
    @Bean
    @ConditionalOnMissingBean(MultiDbAdaptor.class)
    public MultiDbAdaptor multiSqlExecutor(JdbcTemplate jdbcTemplate, MultiProperties multiProperties) {
        MultiDbSpringAdaptor executor = new MultiDbSpringAdaptor(jdbcTemplate);
        MultiExecutorInner.executor = executor;
        MultiExecutorInner.multiProperties = multiProperties;
        return executor;
    }
}
