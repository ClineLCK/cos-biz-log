package com.coco.terminal.cocobizlog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * es 配置参数
 *
 * @author ckli01
 * @date 2019-03-28
 */
@Data
@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class EsProperties {


    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 组合
     */
    private String scheme = "http";


}

    
    
  