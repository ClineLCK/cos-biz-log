package com.coco.framework.cosbizlog.bean;

/**
 * 业务日志模块枚举
 *
 * @author clinechen
 * @date 2018/8/31
 */
public enum LogModuleEnum {

    DEFAULT(0,"系统"),


    ;

    private Integer type;

    private String desc;

    LogModuleEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
