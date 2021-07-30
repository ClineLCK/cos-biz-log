package com.coco.terminal.cocobizlog.entity;

import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import com.coco.terminal.cocobizlog.util.TimeZoneUtil;

/**
 * 传输到 es 去的 日志实体
 *
 * @author ckli01
 * @date 2019-05-23
 */
public class EsLogEntity extends LogEntityDO {


    public String getShangHaiTimestamp() {
        return TimeZoneUtil.getShangHaiTime(getDate());
    }

    public String getUTCTimestamp() {
        return TimeZoneUtil.getUTCTime(getDate());
    }

}

    
    
  