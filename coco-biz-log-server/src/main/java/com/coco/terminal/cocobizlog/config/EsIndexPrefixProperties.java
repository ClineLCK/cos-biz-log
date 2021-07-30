package com.coco.terminal.cocobizlog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * es 索引前缀属性配置
 *
 * @author ckli01
 * @date 2019-09-18
 */
@Data
@Component
@ConfigurationProperties(prefix = "coco.biz.log")
public class EsIndexPrefixProperties {



    private String esPrefix;



}

    
    
  