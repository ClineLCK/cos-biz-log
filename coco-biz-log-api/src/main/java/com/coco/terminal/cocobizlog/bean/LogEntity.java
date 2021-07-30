package com.coco.terminal.cocobizlog.bean;


import lombok.Data;

import javax.validation.constraints.NotNull;
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
    @NotNull
    private Integer module;

    /**
     * 日志事件
     */
    @NotNull
    private Integer event;

    /**
     * 服务名称
     */
    @NotNull
    private String serviceName;

    /**
     * 服务器Ip地址
     */
    private String machineIp;

    /**
     * 操作人
     */
    @NotNull
    private String operId;

    /**
     * 操作日期
     */
    @NotNull
    private Date date;

    /**
     * 操作人Ip地址
     */
    private String operIp;

}
