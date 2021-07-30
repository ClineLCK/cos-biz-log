package com.coco.terminal.cocobizlog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * rocketMq 生产者配置类
 *
 * @author ckli01
 * @date 2018/9/3
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "coco.bizlog.rocketMq.producer")
public class RocketMqProducerConfig {

    /**
     * 生产者组名
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
     * 发送失败重试次数
     */
    private int retryTimes = 2;

}
