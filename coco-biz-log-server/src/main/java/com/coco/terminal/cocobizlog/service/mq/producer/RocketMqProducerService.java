package com.coco.terminal.cocobizlog.service.mq.producer;


import java.util.Map;

/**
 * rocketMq 生产者基础服务
 *
 * @author ckli01
 * @date 2018/8/31
 */
public interface RocketMqProducerService {


    /**
     * 将发送数据添加到队列
     *
     * @param map
     */
    void addLogEntity(Map map);


}
