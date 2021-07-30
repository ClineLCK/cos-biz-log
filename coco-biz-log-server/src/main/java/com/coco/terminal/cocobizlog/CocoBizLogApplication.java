package com.coco.terminal.cocobizlog;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author clinechen
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@EnableScheduling
@EnableApolloConfig
public class CocoBizLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CocoBizLogApplication.class, args);
    }
}
