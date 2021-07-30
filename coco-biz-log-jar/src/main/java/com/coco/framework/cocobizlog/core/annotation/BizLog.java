package com.coco.framework.cocobizlog.core.annotation;

import com.coco.framework.cocobizlog.common.CosBizLogConstant;
import com.coco.framework.cocobizlog.core.enums.LogEventEnum;
import com.coco.framework.cocobizlog.core.enums.LogModuleEnum;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 业务日志注解 logModule 若配置 则按这个来，否则取name、type 为日志模块名称以及模块对应类型 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizLog {

  /**
   * 日志模块 默认系统日志模块，可在方法以及类上申明，优先级为方法>类>默认
   *
   * @return
   */
  LogModuleEnum logModule() default LogModuleEnum.DEFAULT;

  /**
   * 自定义 日志模块枚举 名称
   *
   * @return
   */
  String moduleEnumName() default CosBizLogConstant.EMPTY;

  /**
   * 日志事件
   *
   * @return
   */
  LogEventEnum logEvent() default LogEventEnum.DEFAULT;

  /**
   * 自定义 日志事件枚举 名称
   *
   * @return
   */
  String eventEnumName() default CosBizLogConstant.EMPTY;

  /**
   * 是否触发事件特有处理
   *
   * @return
   */
  boolean touch() default true;
}
