package com.coco.terminal.cocobizlog.entity;

import lombok.Data;

/**
 * 企业微信报警实体
 *
 * @author ckli01
 * @date 2019-08-16
 */
@Data
public class WeChatWorkAlarmEntity {


    /**
     * 消息类型
     */
    private String msgtype = "markdown";


    private WeChatWorkAlarmTextEntity markdown;


}

    
    
  