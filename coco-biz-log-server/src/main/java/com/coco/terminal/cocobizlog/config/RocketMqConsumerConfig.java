package com.coco.terminal.cocobizlog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * rocketMq 消费者配置类
 *
 * @author ckli01
 * @date 2018/8/31
 */
@Component
@ConfigurationProperties(prefix = "coco.bizlog.rocketMq.consumer")
@Getter
@Setter
public class RocketMqConsumerConfig {

    /**
     * 消费者组名
     */
    private String groupName;
    /**
     * mq 地址
     */
    private String namesrvAddr;

    /**
     * topic
     */
    private String topic;

    /**
     * 消费者最少线程数
     */
    private int threadMin = 20;
    /**
     * 消费者最多线程数
     */
    private int threadMax = 64;
    /**
     * 一次消费消息数量
     */
    private int messageBatchMaxSize = 1;

    /**
     * 重复消费次数
     */
    private int retryTimes = 3;


}
