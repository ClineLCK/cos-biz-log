package com.coco.framework.cocobizlog.bean;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 日志 事件 记录 实体类
 *
 * @author ckli01
 * @date 2019-05-21
 */
@Data
public class LogEventEntity {

  /** 字段变更 集合 */
  private List<LogEventFieldEntity> list;


  /**
   * 自定义查询参数
   */
  private Map<String,String> searchMap;


}
