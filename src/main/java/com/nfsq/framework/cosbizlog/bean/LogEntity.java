package com.nfsq.framework.cosbizlog.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * 日志类
 *
 * @author ckli01
 * @date 2018/8/31
 */
@Data
public class LogEntity {

    private Long id;

    /**
     * 日志模块
     */
    private Integer module;

    /**
     * 日志事件
     */
    private Integer event;

    /**
     * 日志实体
     */
    private String entity;

    /**
     * 日志JSON 形式
     */
    private JSONObject entityJson;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务器Ip地址
     */
    private String machineIp;

    /**
     * 操作人
     */
    private Long operId;

    /**
     * 操作日期
     */
    private Date date;

    /**
     * 操作人Ip地址
     */
    private String operIp;


}
