package com.coco.framework.cosbizlog.service;


import com.coco.framework.cosbizlog.bean.CurrentLoginUserInfo;

/**
 * 基础业务服务类
 *
 * @author clinechen
 * @date 2018/6/27
 */
public interface BizLogBaseService<T> {


    /**
     * 根据主键id获取数据
     *
     * @param id
     * @return
     */
    T getById(Object id) throws Exception;

    /**
     * 获取当前登录用户信息
     * @return
     */
    CurrentLoginUserInfo getCurrentLoginUserInfo();
}
