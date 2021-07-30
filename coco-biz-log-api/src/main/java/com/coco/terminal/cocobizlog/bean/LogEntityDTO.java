package com.coco.terminal.cocobizlog.bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 日志 DTO
 *
 * @author ckli01
 * @date 2019-05-22
 */
@Data
public class LogEntityDTO extends LogEntity {


    /**
     * 日志实体
     */
    private List<String> entitys;

    /**
     * 日志JSON 形式
     */
    private List<Map<Object, Object>> entityJsons;

    /**
     * 日志 自定义业务查询参数
     */
    private Map<String, String> searchMap;

}

    
    
  