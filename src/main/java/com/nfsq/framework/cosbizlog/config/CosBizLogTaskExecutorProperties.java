package com.nfsq.framework.cosbizlog.config;

import lombok.Getter;
import lombok.Setter;

/**
 * 线程池属性配置类
 *
 * @author ckli01
 * @date 2018/7/2
 */
@Getter
@Setter
public class CosBizLogTaskExecutorProperties {


    /**
     * 核心线程数
     */
    private Integer corePoolSize = 4;

    /**
     * 任务队列最大值
     */
    private Integer queueCapacity = 60;

    /**
     * 最大运行线程数
     */
    private Integer maxPoolSize = 30;

    /**
     * 线程存活时间
     */
    private Integer keepAliveTime = 60000;


}
