package com.coco.framework.cosbizlog.bean;

import java.util.Date;

/**
 * 日志类
 *
 * @author clinechen
 * @date 2018/8/31
 */
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getModule() {
        return module;
    }

    public void setModule(Integer module) {
        this.module = module;
    }

    public Integer getEvent() {
        return event;
    }

    public void setEvent(Integer event) {
        this.event = event;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOperIp() {
        return operIp;
    }

    public void setOperIp(String operIp) {
        this.operIp = operIp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }
}
