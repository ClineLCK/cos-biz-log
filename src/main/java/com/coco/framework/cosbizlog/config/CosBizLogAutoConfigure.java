package com.coco.framework.cosbizlog.config;

import com.coco.framework.cosbizlog.aop.BizLogAop;
import com.coco.framework.cosbizlog.util.CosBizLogSpringContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 启动注册类
 *
 * @author clinechen
 * @date 2018/9/28
 */
@Configuration
public class CosBizLogAutoConfigure {

    @Bean
    @ConditionalOnMissingBean(BizLogAop.class)
    public BizLogAop bizLogAop() {
        return new BizLogAop();
    }

    @Bean
    @ConditionalOnMissingBean(CosBizLogSpringContext.class)
    public CosBizLogSpringContext cosBizLogSpringContext() {
        return new CosBizLogSpringContext();
    }

    @Bean
    @ConfigurationProperties(prefix = "cos.bizlog.executor")
    @ConditionalOnMissingBean
    public CosBizLogTaskExecutorProperties cosBizLogTaskExecutorProperties() {
        return new CosBizLogTaskExecutorProperties();
    }

    @Bean
    @ConditionalOnMissingBean(value = {Executor.class, ThreadPoolTaskExecutor.class})
    public Executor executor(CosBizLogTaskExecutorProperties cosBizLogTaskExecutorProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(cosBizLogTaskExecutorProperties.getCorePoolSize());

        executor.setMaxPoolSize(cosBizLogTaskExecutorProperties.getMaxPoolSize());

        executor.setQueueCapacity(cosBizLogTaskExecutorProperties.getQueueCapacity());

        executor.setKeepAliveSeconds(cosBizLogTaskExecutorProperties.getKeepAliveTime());

        executor.setThreadNamePrefix("cos-biz-log");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();

        return executor;
    }

}
