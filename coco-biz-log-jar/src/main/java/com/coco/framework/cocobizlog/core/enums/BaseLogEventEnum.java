package com.coco.framework.cocobizlog.core.enums;

import com.coco.framework.cocobizlog.core.event.EventOperator;

/**
 * 基础日志事件枚举 接口
 *
 * @author ckli01
 * @date 2019-03-27
 */
public interface BaseLogEventEnum extends BaseLogEnum {

  Class<? extends EventOperator> getOperatorClazz();
}
