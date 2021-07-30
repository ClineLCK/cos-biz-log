package com.coco.terminal.cocobizlog.util;

import com.coco.terminal.cocobizlog.bean.LogEntity;
import com.coco.terminal.cocobizlog.config.EsIndexPrefixProperties;

/**
 * 索引工具类
 *
 * @author ckli01
 * @date 2019-09-18
 */
public class EsIndexUtil {


    /**
     * 获取 业务 日志 es 索引名称
     *
     * @param logEntityDO
     * @param <T>
     * @return
     */
    public static <T extends LogEntity> String index(T logEntityDO) {
        StringBuilder stringBuilder = new StringBuilder(esPrefix());
        stringBuilder.append("-");


        stringBuilder.append(logEntityDO.getServiceName());
        stringBuilder.append("-");
        stringBuilder.append(logEntityDO.getModule());

        return stringBuilder.toString();
    }


    /**
     * 获取 业务 日志 es 索引名称
     *
     * @param logEntityDO
     * @param <T>
     * @return
     */
    public static <T extends LogEntity> String[] indexes(T logEntityDO) {

        if (logEntityDO.getServiceName().contains(",")) {
            String[] serviceNames = logEntityDO.getServiceName().split(",");

            String[] indexes = new String[serviceNames.length];
            int i = 0;
            for (String str : serviceNames) {
                StringBuilder stringBuilder = new StringBuilder(esPrefix());
                stringBuilder.append("-");
                stringBuilder.append(str);
                stringBuilder.append("-");
                stringBuilder.append(logEntityDO.getModule());

                indexes[i++] = stringBuilder.toString();
            }
            return indexes;
        } else {
            String[] indexes = new String[1];
            StringBuilder stringBuilder = new StringBuilder(esPrefix());
            stringBuilder.append("-");


            stringBuilder.append(logEntityDO.getServiceName());
            stringBuilder.append("-");
            stringBuilder.append(logEntityDO.getModule());

            indexes[0]=stringBuilder.toString();

            return indexes;
        }


    }


    /**
     * 获取es 索引前缀
     *
     * @return
     */
    private static String esPrefix() {
        EsIndexPrefixProperties esIndexPrefixProperties = CocoSpringContext.getBean(EsIndexPrefixProperties.class);
        return esIndexPrefixProperties.getEsPrefix();
    }


}

    
    
  