package com.coco.framework.cocobizlog.core;

import com.coco.framework.cocobizlog.common.CosBizLogConstant;
import com.coco.framework.cocobizlog.util.WrappedBeanCopier;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志服务 实体 功能类 需要写日志服务且需要自定义日志实体内容的类，重写prefixBizLogStr()
 *
 * @author ckli01
 * @date 2018/9/3
 */
@Slf4j
public abstract class BizLogStr {

  /**
   * 获取主键Id
   *
   * @return
   */
  public abstract Object cocoKey();

  /**
   * 获取自定义日志取值,前缀
   *
   * @return
   */
  public String prefixBizLogStr() {
    return cocoKey() != null
        ? cocoKey().toString()
        : CosBizLogConstant.EMPTY;
  }

  /**
   * 获取自定义日志取值,后缀
   *
   * @return
   */
  public String suffixBizLogStr() {
    return CosBizLogConstant.EMPTY;
  }

  /**
   * 转换类型 DO -> VO
   *
   * @param clazz
   * @return
   */
  public Object convertDoToVo(Class<?> clazz) {
    return WrappedBeanCopier.copyProperties(this, clazz);
  }
}
