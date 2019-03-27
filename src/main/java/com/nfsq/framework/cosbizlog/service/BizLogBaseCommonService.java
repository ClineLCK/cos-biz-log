package com.nfsq.framework.cosbizlog.service;

import com.nfsq.framework.cosbizlog.bean.CurrentLoginUserInfo;
import com.nfsq.framework.cosbizlog.enums.BaseLogEventEnum;
import com.nfsq.framework.cosbizlog.enums.BaseLogModuleEnum;

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
     * @param enumName
     * @return
     */
    BaseLogEventEnum logEventEnum(String enumName);

    /**
     * 获取 日志模块枚举需要调用的枚举类
     * @param enumName
     * @return
     */
    BaseLogModuleEnum logModuleEnum(String enumName);

}
