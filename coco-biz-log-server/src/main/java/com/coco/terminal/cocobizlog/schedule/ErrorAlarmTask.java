package com.coco.terminal.cocobizlog.schedule;

import com.alibaba.fastjson.JSONObject;
import com.coco.terminal.cocobizlog.entity.ErrorLogAlarmEntity;
import com.coco.terminal.cocobizlog.entity.WeChatWorkAlarmEntity;
import com.coco.terminal.cocobizlog.entity.WeChatWorkAlarmTextEntity;
import com.coco.terminal.cocobizlog.service.es.AlarmBaseService;
import com.coco.terminal.cocobizlog.util.RestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 错误告警
 *
 * @author ckli01
 * @date 2019-08-15
 */
@Component
@Slf4j
public class ErrorAlarmTask {


    @Autowired
    private AlarmBaseService alarmBaseService;

    /**
     * 报警机器人地址
     */
    private static final String WECHAT_WORK_ALARM_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=a8e81d51-7789-4e83-8b40-4858ceb747eb";

    /**
     * 每隔 10 分钟 触发一次
     *
     * @return
     */
    @Scheduled(cron = "0 */10 * * * *")
    public void errorAlarmSchedule() {
        LocalDateTime now = LocalDateTime.now();
        long endTime = Timestamp.valueOf(now).getTime();
        LocalDateTime tenMinus = now.minusMinutes(10);
        long startTime = Timestamp.valueOf(tenMinus).getTime();

        log.info("{} - {} will statistic the error num of service", startTime, endTime);
        Map<String, List<ErrorLogAlarmEntity>> map = null;
        try {
            map = alarmBaseService.logAlarm(startTime, endTime);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (CollectionUtils.isEmpty(map)) {
            log.info("statistic the error , not need send sms...");
        } else {

            WeChatWorkAlarmTextEntity weChatWorkAlarmTextEntity = new WeChatWorkAlarmTextEntity();


            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("# <font color=\"comment\">警告</font>\n");

            map.forEach((k, v) -> {
                stringBuilder.append("#### 服务名称：");
                stringBuilder.append(k);
                stringBuilder.append("\n");

                v.forEach(a -> {
                    stringBuilder.append(">时间：");
                    stringBuilder.append(a.getAlarmTime());
                    stringBuilder.append("\n");

                    stringBuilder.append("错误数：<font color=\"comment\">");
                    stringBuilder.append(a.getCount());
                    stringBuilder.append("</font>\n");
                });
                stringBuilder.append("\n");

            });

            weChatWorkAlarmTextEntity.setContent(stringBuilder.toString());


            WeChatWorkAlarmEntity weChatWorkAlarmEntity = new WeChatWorkAlarmEntity();
            weChatWorkAlarmEntity.setMarkdown(weChatWorkAlarmTextEntity);

            log.info("send sms for : {}", JSONObject.toJSONString(weChatWorkAlarmEntity));

            RestUtils.post(WECHAT_WORK_ALARM_URL, weChatWorkAlarmEntity);
        }


    }




}

    
    
  