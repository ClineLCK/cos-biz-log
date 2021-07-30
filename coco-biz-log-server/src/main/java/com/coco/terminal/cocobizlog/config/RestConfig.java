package com.coco.terminal.cocobizlog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * restTemplate 配置类
 *
 * @author ckli01
 * @date 2018/7/4
 */
@Configuration
public class RestConfig {


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
