package com.coco.terminal.cocobizlog.service.biz;


import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import com.coco.terminal.cocobizlog.entity.EsEntity;

import java.util.List;

/**
 * 日志服务类
 *
 * @author ckli01
 * @date 2018/8/31
 */
public interface LogEntityService
//        extends BaseService<LogEntityDO>
{
    void dealDataToDBAndEs(List<LogEntityDO> logEntities, List<EsEntity> esEntities) throws Exception;


    /**
     * 初始化表
     *
     * @param year
     * @param month
     */
    void initTable(Integer year, Integer month);


    /**
     * 判断表是否存在
     *
     * @param year
     * @param month
     * @return
     */
    boolean existTable(int year, int month);


    boolean dealMsg(List<String> msgs);

}
