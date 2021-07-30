package com.coco.terminal.cocobizlog.controller;


import com.coco.terminal.cocobizlog.bean.HttpRestResult;
import com.coco.terminal.cocobizlog.exception.IResultCode;
import com.coco.terminal.cocobizlog.exception.ResultCodes;


/**
 * 常用http返回
 *
 * @author ckli01
 * @date 2019/9/29
 */
public abstract class AbstractBaseController {

    /**
     * 返回成功
     */
    public static <T> HttpRestResult<T> responseOK(IResultCode code, String message, T data) {
        HttpRestResult<T> restResult = new HttpRestResult<>();
        restResult.setSuccess(true);
        restResult.setData(data);
        restResult.setCode(code.getCode());
        message = message == null ? code.getText() : message;
        restResult.setMessage(message);
        return restResult;
    }

    /**
     * 默认成功返回
     */
    public static <T> HttpRestResult<T> responseOK(T data) {
        return responseOK(ResultCodes.OK, null, data);
    }

    public static <T> HttpRestResult<T> responseOK() {
        return responseOK(ResultCodes.OK, null, null);
    }

    /**
     * 返回成功，带信息
     */
    public static <T> HttpRestResult<T> responseOK(IResultCode code, String message) {
        HttpRestResult<T> restResult = new HttpRestResult<>();
        restResult.setSuccess(true);
        restResult.setData(null);
        restResult.setCode(code.getCode());
        message = message == null ? code.getText() : message;
        restResult.setMessage(message);
        return restResult;
    }

    /**
     * 返回业务异常, 不带参数
     */
    public static <T> HttpRestResult<T> responseServiceException() {
        return responseOK(ResultCodes.BUSINESS_ERROR, null, null);
    }

    /**
     * 返回系统异常，带code
     */
    public static <T> HttpRestResult<T> responseServiceException(IResultCode code) {
        return responseOK(code, null, null);
    }

    /**
     * 返回业务异常, 带信息
     */
    public static <T> HttpRestResult<T> responseServiceException(String message) {
        return responseOK(ResultCodes.BUSINESS_ERROR, message, null);
    }

    /**
     * 返回异常，带code，带信息
     */
    public static <T> HttpRestResult<T> responseServiceException(IResultCode code, String message) {
        return responseOK(code, message, null);
    }

    /**
     * 返回系统异常, 不带参数
     */
    public static <T> HttpRestResult<T> responseSystemException() {
        return responseFail(ResultCodes.BUSINESS_ERROR, null, null);
    }

    /**
     * 返回系统异常，带code
     */
    public static <T> HttpRestResult<T> responseSystemException(IResultCode code) {
        return responseFail(code, null, null);
    }

    /**
     * 返回系统异常, 带信息
     */
    public static <T> HttpRestResult<T> responseSystemException(String message) {
        return responseFail(ResultCodes.BUSINESS_ERROR, null, message);
    }

    /**
     * 返回系统异常，带code，带信息
     */
    public static <T> HttpRestResult<T> responseSystemException(IResultCode code, String message) {
        return responseFail(code, null, message);
    }

    /**
     * 失败返回
     *
     * @param code    错误Code
     * @param data    数据内容，可为null
     * @param message 若为null，则使用Code对应的默认信息
     * @return
     */
    public static <T> HttpRestResult<T> responseFail(IResultCode code, T data, String message) {
        HttpRestResult<T> restResult = new HttpRestResult<>();
        restResult.setSuccess(false);
        restResult.setData(data);
        restResult.setCode(code.getCode());
        message = message == null ? code.getText() : message;
        restResult.setMessage(message);
        return restResult;
    }

}
