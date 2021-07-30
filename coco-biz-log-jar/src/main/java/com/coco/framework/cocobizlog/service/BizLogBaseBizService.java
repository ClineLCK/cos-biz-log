package com.coco.framework.cocobizlog.service;

import java.util.List;
import java.util.Map;

/**
 * 日志相关基础业务服务类
 *
 * @author ckli01
 * @date 2018/6/27
 */
public interface BizLogBaseBizService {

    /**
     * 根据主键id获取数据
     *
     * @param id
     * @return
     */
    Object getPrefixEntityById(Object id) throws Exception;

    /**
     * 根据主键ids 批量获取数据
     *
     * @param ids
     * @return
     */
    List getPrefixEntityByIds(List ids) throws Exception;


    /**
     * 查询条件
     *
     * @param
     * @return
     */
    Map<String, String> getSearchEntity(Object obj) throws Exception;

}
