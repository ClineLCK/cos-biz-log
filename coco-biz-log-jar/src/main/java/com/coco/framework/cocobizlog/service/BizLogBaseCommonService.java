package com.coco.framework.cocobizlog.service;

import com.coco.framework.cocobizlog.core.enums.BaseLogEventEnum;
import com.coco.framework.cocobizlog.core.enums.BaseLogModuleEnum;
import com.coco.framework.cocobizlog.bean.CurrentLoginUserInfo;

/**
 * 日志相关基础人员业务服务类
 *
 * @author ckli01
 * @date 2019-03-27
 */
public interface BizLogBaseCommonService {

  /**
   * 获取当前登录用户信息
   *
   * @return
   */
  CurrentLoginUserInfo getCurrentLoginUserInfo();

  /**
   * 获取 日志事件枚举需要调用的枚举类
   *
   * @param enumName
   * @return
   */
  BaseLogEventEnum logEventEnum(String enumName);

  /**
   * 获取 日志模块枚举需要调用的枚举类
   *
   * @param enumName
   * @return
   */
  BaseLogModuleEnum logModuleEnum(String enumName);
}
