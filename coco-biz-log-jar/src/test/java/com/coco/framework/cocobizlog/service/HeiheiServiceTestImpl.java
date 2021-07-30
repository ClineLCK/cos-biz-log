package com.coco.framework.cocobizlog.service;

import com.coco.framework.cocobizlog.bean.CurrentLoginUserInfo;
import com.coco.framework.cocobizlog.core.enums.BaseLogEventEnum;
import com.coco.framework.cocobizlog.core.enums.BaseLogModuleEnum;
import com.coco.framework.cocobizlog.core.enums.LogEventEnum;
import com.coco.framework.cocobizlog.core.enums.LogModuleEnum;
import org.springframework.stereotype.Service;

/**
 * @author ckli01
 * @date 2019-03-26
 */
@Service
public class HeiheiServiceTestImpl implements BizLogBaseCommonService {

  @Override
  public CurrentLoginUserInfo getCurrentLoginUserInfo() {

    CurrentLoginUserInfo currentLoginUserInfo = new CurrentLoginUserInfo();
    currentLoginUserInfo.setId("1112");
    currentLoginUserInfo.setIp("127.1.1.1");
    currentLoginUserInfo.setName("lck");

    return currentLoginUserInfo;
  }

  @Override
  public BaseLogEventEnum logEventEnum(String enumName) {
    return LogEventEnum.valueOf(enumName);
  }

  @Override
  public BaseLogModuleEnum logModuleEnum(String enumName) {
    return LogModuleEnum.valueOf(enumName);
  }
}
