package com.coco.framework.cocobizlog.core.event.impl;

import com.coco.framework.cocobizlog.bean.LogEventEntity;
import com.coco.framework.cocobizlog.bean.LogEventFieldEntity;
import com.coco.framework.cocobizlog.core.BizLogStr;
import com.coco.framework.cocobizlog.core.enums.LogEventFieldTypeEnum;
import com.coco.framework.cocobizlog.core.event.AbstractEventOperator;
import com.coco.framework.cocobizlog.core.event.EventOperator;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 批量删除
 *
 * @author ckli01
 * @date 2019/11/21
 */
@Component
@Slf4j
public class BatchDeleteEventOperator extends AbstractEventOperator implements EventOperator {

  @Override
  public List<LogEventEntity> operate(Object[] args) {
    List<LogEventEntity> list = new ArrayList<>();

    if (args[0] != null) {
      // 字段变更 记录
      if (args[0] instanceof List) {
        List<Object> objects = (List<Object>) args[0];
        for (Object o : objects) {
          LogEventEntity logEventEntity = new LogEventEntity();
          List<LogEventFieldEntity> fieldEntities = new ArrayList<>();
          LogEventFieldEntity logEventFieldEntity = new LogEventFieldEntity();

          logEventFieldEntity.setFiledName("bizlog_prefix");
          logEventFieldEntity.setFiledNameZh("关键字");
          logEventFieldEntity.setLogEventFieldTypeEnum(LogEventFieldTypeEnum.BODY);

          if (o instanceof BizLogStr) {
            Object cocoKey = ((BizLogStr) o).cocoKey();
            logEventFieldEntity.setResult(cocoKey != null ? cocoKey.toString() : "");
          } else {
            logEventFieldEntity.setResult(o != null ? o.toString() : "");
          }
          fieldEntities.add(logEventFieldEntity);
          logEventEntity.setList(fieldEntities);
          logEventEntity.setSearchMap(super.searchMap(o));
          list.add(logEventEntity);
        }
      } else {
        LogEventEntity logEventEntity = new LogEventEntity();
        List<LogEventFieldEntity> fieldEntities = new ArrayList<>();
        LogEventFieldEntity logEventFieldEntity = new LogEventFieldEntity();

        logEventFieldEntity.setFiledName("bizlog_prefix");
        logEventFieldEntity.setFiledNameZh("关键字");
        logEventFieldEntity.setLogEventFieldTypeEnum(LogEventFieldTypeEnum.BODY);
        logEventFieldEntity.setResult(args[0] != null ? args[0].toString() : "");

        fieldEntities.add(logEventFieldEntity);
        logEventEntity.setList(fieldEntities);
        logEventEntity.setSearchMap(super.searchMap(args[0]));
        list.add(logEventEntity);
      }
    }
    return list;
  }


}

    
    
  