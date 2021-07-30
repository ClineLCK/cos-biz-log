package com.coco.framework.cocobizlog.core.event.impl;

import com.coco.framework.cocobizlog.bean.LogEventEntity;
import com.coco.framework.cocobizlog.core.event.AbstractEventOperator;
import com.coco.framework.cocobizlog.core.event.EventOperator;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 默认 系统 处理
 *
 * @author ckli01
 * @date 2019-05-20
 */
@Component
public class DefaultEventOperator extends AbstractEventOperator implements EventOperator {

  @Override
  public List<LogEventEntity> operate(Object[] args) {
    return super.operate(args);
  }
}
