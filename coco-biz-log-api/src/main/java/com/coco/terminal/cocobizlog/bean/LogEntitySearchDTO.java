package com.coco.terminal.cocobizlog.bean;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 日志查询类
 *
 * @author ckli01
 * @date 2019-09-03
 */
@Data
public class LogEntitySearchDTO extends Page{

    /**
     * 日志基本信息
     */
    private LogEntityBaseSearchDTO baseSearchDTO;

    /**
     * 日志自定义实体
     */
    private Map<String, Object> entityJson;

    /**
     * 日志 前后缀 查询 条件 自定义实体
     */
    private List<String> prefixSuffix;


}

    
    
  