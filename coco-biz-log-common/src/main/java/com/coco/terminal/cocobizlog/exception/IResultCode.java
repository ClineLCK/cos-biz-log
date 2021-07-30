package com.coco.terminal.cocobizlog.exception;

public interface IResultCode {
    /**
     *  状态码
     *
     * @return
     */
    String getCode();

    /**
     * 状态描述
     *
     * @return
     */
    String getText();
}
