package com.coco.terminal.cocobizlog.exception;


/**
 * Service层服务处理异常
 *
 * @author ckli01
 * @date 2018/10/18
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -4827639322754357173L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static void throwException(String message) {
        throw new ServiceException(message);
    }

}
