package com.coco.framework.cocobizlog.core.event.impl;

import com.coco.framework.cocobizlog.bean.LogEventEntity;
import com.coco.framework.cocobizlog.core.event.AbstractEventOperator;
import com.coco.framework.cocobizlog.core.event.EventOperator;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 批量添加 事件 操作
 *
 * @author ckli01
 * @date 2019-05-21
 */
@Component
@Slf4j
public class BatchAddEventOperator extends AbstractEventOperator implements EventOperator {

  @Autowired private AddEventOperator addEventOperator;

  @Override
  public List<LogEventEntity> operate(Object[] args) {
    List<LogEventEntity> list = new ArrayList<>();
    try {
      if (args[0] != null) {

        if (args[0] instanceof List) {
          List<Object> objects = (List<Object>) args[0];

          for (Object o : objects) {

            List<LogEventEntity> entities = addEventOperator.operate(o);
            if (!CollectionUtils.isEmpty(entities)) {
              list.addAll(entities);
            }
          }
        }
      }
    } catch (Exception e) {
      log.warn("BatchAddEventOperator  batch add biz log error for : {} ", e.getMessage(), e);
    }
    return list;
  }
}
