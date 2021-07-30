package com.coco.framework.cocobizlog.core.enums;

import com.coco.framework.cocobizlog.core.event.EventOperator;
import com.coco.framework.cocobizlog.core.event.impl.AddEventOperator;
import com.coco.framework.cocobizlog.core.event.impl.BatchAddEventOperator;
import com.coco.framework.cocobizlog.core.event.impl.BatchDeleteEventOperator;
import com.coco.framework.cocobizlog.core.event.impl.BatchUpdateEventOperator;
import com.coco.framework.cocobizlog.core.event.impl.DefaultEventOperator;
import com.coco.framework.cocobizlog.core.event.impl.DeleteEventOperator;
import com.coco.framework.cocobizlog.core.event.impl.UpdateEventOperator;

/**
 * 日志事件枚举
 *
 * @author ckli01
 * @date 2018/8/31
 */
public enum LogEventEnum implements BaseLogEventEnum {
  DEFAULT(0, "QAQ", DefaultEventOperator.class),
  ADD(1, "添加", AddEventOperator.class),
  UPDATE(2, "更新", UpdateEventOperator.class),
  DELETE(3, "删除", DeleteEventOperator.class),

  SUBMIT(4, "提交", DefaultEventOperator.class),
  LOGIN(5, "登录", DefaultEventOperator.class),
  LOGOUT(6, "登出", DefaultEventOperator.class),
  BATCH_ADD(7, "批量插入", BatchAddEventOperator.class),
  BATCH_UPDATE(8, "批量更新", BatchUpdateEventOperator.class),
  BATCH_DELETE(9, "批量删除", BatchDeleteEventOperator.class),
  ;

  private Integer type;

  private String desc;

  private Class<? extends EventOperator> operatorClazz;

  LogEventEnum(Integer type, String desc, Class<? extends EventOperator> clazz) {
    this.type = type;
    this.desc = desc;
    this.operatorClazz = clazz;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  @Override
  public Class<? extends EventOperator> getOperatorClazz() {
    return operatorClazz;
  }
}
