package com.coco.terminal.cocobizlog.service.es;

import com.coco.terminal.cocobizlog.entity.ErrorLogAlarmEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 报警
 *
 * @author ckli01
 * @date 2019-08-15
 */
public interface AlarmBaseService {


    /**
     * 日志报警
     *
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String,List<ErrorLogAlarmEntity>> logAlarm(Long startTime, Long endTime) throws IOException;


}
