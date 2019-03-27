package com.nfsq.framework.cosbizlog.annotation;


import java.lang.annotation.*;

/**
 * 日志服务 属性校验
 *
 * @author ckli01
 * @date 2018/9/5
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizLogVsField {

    String fieldNameStr() default "";

    String strMethodName() default "";

}
