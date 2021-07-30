package com.coco.terminal.cocobizlog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 线程池属性配置类
 *
 * @author ckli01
 * @date 2018/7/2
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "executor")
public class TaskExecutorProperties {


    /**
     * 核心线程数
     */
    private Integer corePoolSize = 10;

    /**
     * 任务队列最大值
     */
    private Integer queueCapacity = 100;

    /**
     * 最大运行线程数
     */
    private Integer maxPoolSize = 30;

    /**
     * 线程存活时间
     */
    private Integer keepAliveTime = 60000;


}
