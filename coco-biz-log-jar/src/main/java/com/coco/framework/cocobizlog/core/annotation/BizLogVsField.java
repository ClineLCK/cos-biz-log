package com.coco.framework.cocobizlog.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

  boolean childExtend() default false;
}
