package com.coco.terminal.cocobizlog.bean;



import java.io.Serializable;

/**
 * Http 统一返回
 *
 * @author ckli01
 * @date 2018/6/25
 */
public class HttpRestResult<T> implements Serializable {

    private static final long serialVersionUID = -1L;
    private boolean success = false;
    private T data;
    private String code;
    private String message;

    public HttpRestResult() {
    }

    public HttpRestResult(boolean success, T data, String code, String message) {
        this.success = success;
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
