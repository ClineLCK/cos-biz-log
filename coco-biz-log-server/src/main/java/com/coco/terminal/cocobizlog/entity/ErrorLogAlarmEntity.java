package com.coco.terminal.cocobizlog.entity;

import lombok.Data;

/**
 * 错误日志 报警实体
 *
 * @author ckli01
 * @date 2019-08-15
 */
@Data
public class ErrorLogAlarmEntity {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 数量
     */
    private Long count;

    /**
     * 报警时间
     */
    private String alarmTime;


}

    
    
  