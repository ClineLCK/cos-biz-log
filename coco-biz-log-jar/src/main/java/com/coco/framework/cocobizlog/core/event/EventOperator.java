package com.coco.framework.cocobizlog.core.event;

import com.coco.framework.cocobizlog.bean.LogEventEntity;
import java.util.List;

/**
 * 事件操作
 *
 * @author ckli01
 * @date 2019-05-20
 */
public interface EventOperator {

  /**
   * 事件操作 处理 返回处理条数
   *
   * @param args
   * @return
   */
  List<LogEventEntity> operate(Object[] args);

  /**
   * 事件操作 处理 返回处理条数
   *
   * @param o
   * @return
   */
  List<LogEventEntity> operate(Object o);
}
