package com.nfsq.framework.cosbizlog.enums;

/**
 * 日志事件枚举
 *
 * @author ckli01
 * @date 2018/8/31
 */
public enum LogEventEnum implements BaseLogEventEnum {


    DEFAULT(0, "QAQ"),
    ADD(1, "添加"),
    UPDATE(2, "更新"),
    DELETE(3, "删除"),
    SUBMIT(4, "提交"),
    LOGIN(5, "登录"),
    LOGOUT(6, "登出"),

    ;

    private Integer type;

    private String desc;

    LogEventEnum(Integer type, String desc) {
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
