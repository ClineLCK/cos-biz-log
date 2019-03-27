package com.nfsq.framework.cosbizlog.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 加载 配置文件中 日志模块类
 *
 * @author ckli01
 * @date 2019-03-26
 */
@Component
@ConfigurationProperties(prefix = "bizlog")
@Configuration
@Data
public class BizLogModuleLoad {


//    @Value("${bizlog.module}")
//    private Object bizLogModuleMap;
//    private Map<Object, Map<Object, Object>> bizLogModuleMap;

    private List<Map<String, Object>> modules;


    @PostConstruct
    public void init() {
        System.out.println();
    }


}

    
    
  