package com.coco.terminal.cocobizlog.bean;

import lombok.Data;

import java.util.Map;

/**
 * 日志 DO
 *
 * @author ckli01
 * @date 2019-05-22
 */
@Data
public class LogEntityDO extends LogEntity {

    /**
     * 日志实体
     */
    private String entity;

    private Map<Object, Object> entityJson;

    /**
     * 查询体
     */
    private Map<String,String> searchMap;


}

    
    
  