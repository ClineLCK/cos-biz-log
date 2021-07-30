package com.coco.framework.cosbizlog.annotation;


import com.coco.framework.cosbizlog.bean.LogEventEnum;
import com.coco.framework.cosbizlog.bean.LogModuleEnum;

import java.lang.annotation.*;


/**
 * 业务日志注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizLog {


    /**
     * 日志模块
     * 默认系统日志模块，可在方法以及类上申明，优先级为方法>类>默认
     *
     * @return
     */
    LogModuleEnum logModule() default LogModuleEnum.DEFAULT;

    /**
     * 日志事件
     *
     * @return
     */
    LogEventEnum logEvent();

}
