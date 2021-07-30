package com.coco.terminal.cocobizlog.bean;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 日志查询基础类
 *
 * @author ckli01
 * @date 2019-09-18
 */
@Data
public class LogEntityBaseSearchDTO extends LogEntity {


    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 自定义查询字段
     */
    private Map<String,Object> searchMap;


}

    
    
  