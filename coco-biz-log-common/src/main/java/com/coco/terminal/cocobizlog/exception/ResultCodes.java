package com.coco.terminal.cocobizlog.exception;

/**
 * @Descriptions: 结果编码
 */
public enum ResultCodes implements IResultCode {

    OK("20000", "正常"),

    DATABASE_ERROR("30000", "数据库错误"),
    DATABASE_EFFECT_ROWS_NOT_EXPECT("30001", "数据库影响行数超出预期"),

    PARAMETER_ERROR("40000", "参数错误"),
    PARAMETER_IS_NULL("40001", "参数NULL"),
    PARAMETER_OUT_OF_RANGE("40002", "参数超出范围"),
    PARAMETER_UNKNOW("40003", "参数无法识别"),

    BUSINESS_ERROR("50000", "业务错误"),
    BUSINESS_NOT_EXIST("50001", "数据不存在"),
    BUSINESS_DATA_EXCEPTION("50002", "数据异常"),

    INNER_ERROR("60000", "内部错误"),
    UNKNOW_ERROR("66666", "未知错误"),

    AUTH_FAIL("70003", "鉴权失败"),
    AUTH_INVALID_PRIVILEGE("70004", "非法权限"),
    AUTH_INVALID_USER("70006", "无该用户信息"),
    AUTH_NULL_COOKIES("70007", "用户cookie为空"),
    AUTH_PARAM_TIMESTAMP_NEED("70008", "时间戳参数必须提供"),
    AUTH_PARAM_REQUEST_FROM_NEED("70009", "请求来源必须提供"),
    AUTH_PARAM_OPERATOR_NEED("70010", "必须提供当前操作人工号"),
    AUTH_PARAM_SIGN_NEED("70011", "必须提供加密签名"),
    AUTH_NO_DATA_AUTHORITY("70012", "用户未配置数据权限"),
    AUTH_NO_CURRENT_SYSTEM_TYPE("70013", "未获取到当前系统类型"),
    AUTH_NO_DATA_AUTHORITY_INFORMATION("70014", "数据权限信息不足");

    private String code;

    private String text;

    ResultCodes(String code, String text) {
        this.code = code;
        this.text = text;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getText() {
        return text;
    }
}
