package com.coco.framework.cocobizlog.common;

/**
 * 常量
 *
 * @author ckli01
 * @date 2018/9/28
 */
public class CosBizLogConstant {

    /**
     * 换行符
     */
    public static final String MARK_NEW_LINE = "\r\n";
    /**
     * 空字符串
     */
    public static final String EMPTY = "";

    /**
     * 默认日志模版类型
     */
    public static final int DEFAULT_TYPE = -1;

    /**
     * 发送消息重试次数
     */
    public static final Integer MAX_RETRY_TIMES = 3;

    /**
     * 值之间的间隔
     */
    public static final String VALUE_SPLIT = "->";

    /**
     * 主键区分key 前缀
     */
    public static final String KEY_PREFIX_STR = "id: ";


    /**
     * 子级嵌套 关键key 分割
     */
    public static final String CHILDREN_FIRST_SPLIT = "(-->";
    /**
     * 子级嵌套 关键key 分割
     */
    public static final String CHILDREN_LAST_SPLIT = "<--)";

    /**
     * 查询字段 防止 重复占用，字段定义
     */
    public static final String SEARCH_FIELD_PREFIX = "coco_prefix_";

}
