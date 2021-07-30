package com.coco.terminal.cocobizlog.enums;

/**
 * 事件 记录字段 类型  枚举
 *
 * @author ckli01
 * @date 2019-05-21
 */
public enum LogEventFieldTypeEnum {

    PREFIX(1, "bizlog_prefix"),

    BODY(2, ""),

    SUFFIX(3, "bizlog_suffix"),

    ;


    private Integer order;

    private String filedName;

    LogEventFieldTypeEnum(Integer order, String filedName) {
        this.order = order;
        this.filedName = filedName;
    }


    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }}




