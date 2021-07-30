package com.coco.terminal.cocobizlog.entity;

import lombok.Data;

/**
 * es 消息实体
 *
 * @author ckli01
 * @date 2019-03-28
 */
@Data
public class EsEntity {


    /**
     * 索引名称
     */
    private String index;


    /**
     * 类型
     */
    private String type;


    /**
     * 文档
     */
    private String doc;


    /**
     * id
     */
    private String id;

}

    
    
  