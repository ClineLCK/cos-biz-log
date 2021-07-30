package com.coco.framework.cocobizlog.config;

import com.coco.framework.cocobizlog.mesh.api.OkHttpRemoteApi;
import com.coco.framework.cocobizlog.core.BizLogAop;
import com.coco.framework.cocobizlog.core.BizLogIniter;
import com.coco.framework.cocobizlog.util.CosBizLogSpringContext;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 启动注册类
 *
 * @author ckli01
 * @date 2018/9/28
 */
@Configuration
@ComponentScan(basePackages = "com.coco.framework.cocobizlog")
public class CosBizLogAutoConfigure {

  @Bean
  @ConditionalOnMissingBean(BizLogAop.class)
  public BizLogAop bizLogAop() {
    return new BizLogAop();
  }

  @Bean
  @ConditionalOnMissingBean(OkHttpRemoteApi.class)
  public OkHttpRemoteApi okHttpRemoteApi() {
    return new OkHttpRemoteApi();
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
  @ConditionalOnMissingBean(BizLogIniter.class)
  public BizLogIniter bizLogIniter() {
    return new BizLogIniter();
  }


  @Bean(name = "coco-biz-log-thread")
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
