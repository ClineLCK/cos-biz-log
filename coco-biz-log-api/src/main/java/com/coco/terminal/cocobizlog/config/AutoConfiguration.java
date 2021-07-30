package com.coco.terminal.cocobizlog.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 依赖注入器
 *
 * @author ckli01
 * @date 2019/9/29
 */
@Configuration
@EnableFeignClients(basePackages = {
        "com.coco.terminal.cocobizlog.facade"
       })
public class AutoConfiguration {
}

    
    
  