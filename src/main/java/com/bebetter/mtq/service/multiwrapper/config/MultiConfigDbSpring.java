package com.bebetter.mtq.service.multiwrapper.config;

import com.bebetter.mtq.service.multiwrapper.executor.sqlexecutor.MultiDbAdaptor;
import com.bebetter.mtq.service.multiwrapper.executor.sqlexecutor.MultiDbSpringAdaptor;
import com.bebetter.mtq.service.multiwrapper.executor.MultiExecutorInner;
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
    @ConditionalOnClass(JdbcTemplate.class)
    public MultiDbAdaptor multiSqlExecutor(JdbcTemplate jdbcTemplate) {
        return new MultiDbSpringAdaptor(jdbcTemplate);
    }
}
