package com.nfsq.framework.cosbizlog.service;


/**
 * 日志相关基础业务服务类
 *
 * @author ckli01
 * @date 2018/6/27
 */
public interface BizLogBaseBizService<T> {


    /**
     * 根据主键id获取数据
     *
     * @param id
     * @return
     */
    T getById(Object id) throws Exception;


}
