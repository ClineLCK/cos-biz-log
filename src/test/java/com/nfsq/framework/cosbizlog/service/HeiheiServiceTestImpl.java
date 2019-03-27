package com.nfsq.framework.cosbizlog.service;

import com.nfsq.framework.cosbizlog.bean.CurrentLoginUserInfo;
import com.nfsq.framework.cosbizlog.enums.BaseLogEventEnum;
import com.nfsq.framework.cosbizlog.enums.BaseLogModuleEnum;
import com.nfsq.framework.cosbizlog.enums.LogEventEnum;
import com.nfsq.framework.cosbizlog.enums.LogModuleEnum;
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
        currentLoginUserInfo.setId(1111L);
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



