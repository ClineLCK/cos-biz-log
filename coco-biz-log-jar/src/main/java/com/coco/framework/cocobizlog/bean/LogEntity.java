package com.coco.framework.cocobizlog.bean;

import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * 日志类
 *
 * @author ckli01
 * @date 2018/8/31
 */
@Data
public class LogEntity {

  private Long id;

  /** 日志模块 */
  private Integer module;

  /** 日志事件 */
  private Integer event;

  /** 统一 通用 前缀 module: person event */
  private String commonPrefix;

  private List<LogEventEntity> logEventEntities;

  /** 日志实体 */
  private List<String> entitys;

  /** 日志JSON 形式 */
  private List<JSONObject> entityJsons;

  /** 服务名称 */
  private String serviceName;

  /** 服务器Ip地址 */
  private String machineIp;

  /** 操作人 */
  private String operId;

  /** 操作日期 */
  private Date date;

  /** 操作人Ip地址 */
  private String operIp;
}
