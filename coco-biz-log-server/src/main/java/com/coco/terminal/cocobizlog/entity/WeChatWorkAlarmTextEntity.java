package com.coco.terminal.cocobizlog.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 企业微信 报警实体 文本
 *
 * @author ckli01
 * @date 2019-08-16
 */
@Data
public class WeChatWorkAlarmTextEntity {


    /**
     * 文本内容，最长不超过2048个字节，必须是utf8编码
     */
    private String content;

    @JSONField(name = "mentioned_list")
    private List<String> mentionedList;

    @JSONField(name = "mentioned_mobile_list")
    private List<String> mentionedMobileList;

}

    
    
  