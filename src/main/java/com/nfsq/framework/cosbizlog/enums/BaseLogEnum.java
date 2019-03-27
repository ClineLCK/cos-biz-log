package com.nfsq.framework.cosbizlog.enums;

/**
 * 日志 枚举基本接口
 *
 * @author ckli01
 * @date 2019-03-27
 */
public interface BaseLogEnum {


    /**
     * 获取类型
     *
     * @return
     */
    Integer getType();

    /**
     * 获取描述
     *
     * @return
     */
    String getDesc();

}
