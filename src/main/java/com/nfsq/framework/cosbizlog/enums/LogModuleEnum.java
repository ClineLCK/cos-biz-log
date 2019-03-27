package com.nfsq.framework.cosbizlog.enums;

/**
 * 业务日志模块枚举
 *
 * @author ckli01
 * @date 2018/8/31
 */
public enum LogModuleEnum implements BaseLogModuleEnum{

    DEFAULT(0,"系统"),
    CUST_INFO(1, "门店管理"),
    ROUTE(2, "片区管理"),
    DICT(3,"字典管理"),
    CUST_SHIPPING_ADDR(4,"收货地址管理"),


    WHOLE_VIEW(5, "进店全貌"),

    CUST_EXECUTE(6, "售点执行"),

    ORDER_EXECUTE(7, "订单执行"),

    STOCK_STATISTIC(8, "库存盘点"),

    CUST_ACHIEVEMENT(9, "售点业绩"),

    DISPLAY_EXECUTE(10, "陈列执行"),

    GOODS_INSPECTION(11, "铺货检查"),

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
