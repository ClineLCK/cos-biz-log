package com.coco.terminal.cocobizlog.service.mq.consumer;

import com.google.common.collect.Lists;
import com.coco.terminal.cocobizlog.service.biz.LogEntityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * rocketMq 消息处理器
 *
 * @author ckli01
 * @date 2018/9/3
 */
@Slf4j
@Service
public class RocketMqConsumerMsgListenerProcessor implements MessageListenerConcurrently {

    @Autowired
    private LogEntityService logEntityService;


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        if (CollectionUtils.isEmpty(msgs)) {
            // 空消息直接返回消费成功
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        List<String> list = Lists.newArrayList();
        for (MessageExt messageExt : msgs) {
            String str = new String(messageExt.getBody());
            list.add(str);
            log.info("RocketMqConsumerMsgListenerProcessor consumeMessage msgId is {}", messageExt.getMsgId());
        }
        logEntityService.dealMsg(list);
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }


}
