package com.coco.terminal.cocobizlog.config;

import com.coco.terminal.cocobizlog.exception.ServiceException;
import com.coco.terminal.cocobizlog.util.CocoSpringContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * es 初始化服务
 *
 * @author ckli01
 * @date 2019-03-28
 */
@Slf4j
public class EsInitFactory {


    private static volatile RestHighLevelClient client = null;


    /**
     * 获取 单例 es 操作客户端
     *
     * @return
     */
    public static RestHighLevelClient getClient() {

        if (null != client) {
            return client;
        } else {
            synchronized (EsInitFactory.class) {
                if (null != client) {
                    return client;
                } else {
                    log.info("RestHighLevelClient init create client");
                    EsProperties esProperties = CocoSpringContext.getBean(EsProperties.class);

                    if (StringUtils.isEmpty(esProperties)) {
                        ServiceException.throwException("elasticsearch config may contains some problem...");
                    }
                    String[] hosts = esProperties.getHost().split(";");
                    HttpHost[] hostPorts = new HttpHost[hosts.length];
                    for (int i = 0; i < hosts.length; i++) {
                        String[] hh = hosts[0].split(":");
                        hostPorts[i] = new HttpHost(hh[0], Integer.parseInt(hh[1]), esProperties.getScheme());
                    }
                    client = new RestHighLevelClient(
                            RestClient.builder(hostPorts));
                    return client;
                }
            }
        }
    }

    /**
     * 关闭 es 操作客户端
     *
     * @return
     */
    public static boolean close() {
        if (null != client) {
            try {
                client.close();
                return true;
            } catch (IOException e) {
                log.error("RestHighLevelClient close error for : {}", e.getMessage(), e);
            }
        } else {
            log.info("RestHighLevelClient close error for client is null");
        }
        return false;
    }


}

    
    
  