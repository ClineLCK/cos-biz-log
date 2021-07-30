package com.coco.terminal.cocobizlog.service.mq.consumer;

import com.coco.terminal.cocobizlog.config.RocketMqConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * mq 基础服务实现类
 *
 * @author ckli01
 * @date 2018/8/31
 */
@Service
@Slf4j
public class RocketMqConsumerServiceImpl implements RocketMqConsumerService, InitializingBean {


    @Autowired
    private RocketMqConsumerConfig rocketMqConsumerConfig;

    @Autowired
    private RocketMqConsumerMsgListenerProcessor rocketMqConsumerMsgListenerProcessor;

    private DefaultMQPushConsumer consumer;


    private void init() throws MQClientException {
        consumer = new DefaultMQPushConsumer(rocketMqConsumerConfig.getGroupName());
        consumer.setNamesrvAddr(rocketMqConsumerConfig.getNamesrvAddr());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(rocketMqConsumerConfig.getTopic(), "*");
        //设置一个Listener，主要进行消息的逻辑处理
        consumer.registerMessageListener(rocketMqConsumerMsgListenerProcessor);

        //调用start()方法启动consumer
        consumer.start();

        log.info("DefaultMQPushConsumer start success...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
