package com.nfsq.framework.cosbizlog.annotation;


import com.nfsq.framework.cosbizlog.enums.LogEventEnum;
import com.nfsq.framework.cosbizlog.enums.LogModuleEnum;
import com.nfsq.framework.cosbizlog.util.CosBizLogConstant;

import java.lang.annotation.*;


/**
 * 业务日志注解
 * logModule 若配置 则按这个来，否则取name、type 为日志模块名称以及模块对应类型
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
     * 自定义
     * 日志模块枚举 名称
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
     * 自定义
     * 日志事件枚举 名称
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
