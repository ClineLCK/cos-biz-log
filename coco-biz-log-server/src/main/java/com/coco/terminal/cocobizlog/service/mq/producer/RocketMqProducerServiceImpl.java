package com.coco.terminal.cocobizlog.service.mq.producer;

import com.alibaba.fastjson.JSONObject;
import com.coco.terminal.cocobizlog.service.biz.LogEntityService;
import com.google.common.collect.Lists;
import com.coco.terminal.cocobizlog.config.RocketMqProducerConfig;
import com.coco.terminal.cocobizlog.config.TaskExecutorProperties;
import com.coco.terminal.cocobizlog.util.GzipUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * mq 基础服务实现类
 *
 * @author ckli01
 * @date 2018/8/31
 */
@Slf4j
@Service
public class RocketMqProducerServiceImpl implements RocketMqProducerService, InitializingBean {


    private RocketMqProducerConfig rocketMqProducerConfig;

    private DefaultMQProducer defaultMQProducer;

    private ConcurrentLinkedQueue<Map> concurrentLinkedQueue = new ConcurrentLinkedQueue<>();

    private static final Integer MAX_PRODUCER_COUNT = 10;

    @Autowired
    private Executor executor;

    @Autowired
    private TaskExecutorProperties taskExecutorProperties;

    @Autowired
    private LogEntityService logEntityService;


    @Autowired
    private void setRocketMqProducerConfig(RocketMqProducerConfig rocketMqProducerConfig) {
        this.rocketMqProducerConfig = rocketMqProducerConfig;
    }

    private void init() throws MQClientException {
        defaultMQProducer = new DefaultMQProducer(rocketMqProducerConfig.getGroupName());
        defaultMQProducer.setNamesrvAddr(rocketMqProducerConfig.getNamesrvAddr());
        defaultMQProducer.setRetryTimesWhenSendFailed(rocketMqProducerConfig.getRetryTimes());
        defaultMQProducer.start();
        log.info("init rocketMq producer success...");
        start();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    /**
     * 启动线程处理消息
     */
    public void start() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // 主线程统计当前内存中消息数量
            while (true) {
                try {
                    if (concurrentLinkedQueue.size() > 0 && ((ThreadPoolTaskExecutor) executor).getActiveCount() < taskExecutorProperties.getMaxPoolSize()) {
                        // 开启子线程一次发送消息数量 最多10条
                        executor.execute(() -> {
                            List<Map> logEntities = new ArrayList<>();

                            while (logEntities.size() < MAX_PRODUCER_COUNT && concurrentLinkedQueue.size() > 0) {
                                Map map = concurrentLinkedQueue.poll();
                                if (null != map) {
                                    logEntities.add(map);
                                }
                            }

                            try {
                                if (logEntities.size() > 0) {
                                    send(logEntities);
                                }
                            } catch (Exception e) {
                                log.error("batch send message error : {} \r\n {} \r\n ", e.getMessage(), JSONObject.toJSONString(logEntities), e);
                            }

                        });
                    } else {
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
        log.info("start rocketMq producer thread ...");
    }


    /**
     * 发送信息
     */
    public void send(Map logEntity) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {

        //发送消息
        String str = JSONObject.toJSONString(logEntity);

        Message msg = new Message(rocketMqProducerConfig.getTopic(), str.getBytes());
        //调用producer的send()方法发送消息,重试2次，即最多发送三次
        SendResult sendResult = defaultMQProducer.send(msg);

        defaultMQProducer.send(new ArrayList<Message>());

        if (!SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.error("send logEntity error : {}", str);
        } else {
            log.info("send logEntity success : {}", str);
        }

    }

    /**
     * 批量发送信息
     */
    public void send(List<Map> logEntitys) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {

        if (CollectionUtils.isEmpty(logEntitys)) {
            return;
        }

        List<Message> messages = new ArrayList<>();

        for (Map logEntity : logEntitys) {
            String str = GzipUtil.compress(JSONObject.toJSONString(logEntity));
            // 32K 以上数据大消息
            if (str.length() >= 32 * 1000) {
                // 大消息，本地处理
                executor.execute(() -> logEntityService.dealMsg(Lists.newArrayList(str)));
                continue;
            }
            Message msg = new Message(rocketMqProducerConfig.getTopic(), str.getBytes());
            messages.add(msg);
        }
        //调用producer的send()方法发送消息,重试2次，即最多发送三次
        SendResult sendResult = defaultMQProducer.send(messages);

        if (!SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
            log.error("send logEntity error : {}", JSONObject.toJSONString(logEntitys));
        } else {
            log.info("send logEntity success with msgId {} : {}", sendResult.getMsgId(), JSONObject.toJSONString(logEntitys));
        }

    }


    /**
     * 将发送数据添加到队列
     *
     * @param map
     */
    @Override
    public void addLogEntity(Map map) {
        if (null != map) {
            concurrentLinkedQueue.offer(map);
        }
    }


}
