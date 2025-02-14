package com.example.blog.config;

import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSourceBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof DataSource) {
            return ProxyDataSourceBuilder
                    .create((DataSource) bean)
                    .name("DATA_SOURCE_PROXY")
                    .logQueryBySlf4j()
                    .multiline()
                    .build();
        }
        return bean;
    }
}
