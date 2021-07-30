//
//import java.io.Serializable;
//
///** Created by yjhan6 on 17/7/17. */
//
//public class HttpRestResult<T> implements Serializable {
//
//  private static final long serialVersionUID = -1L;
//  private boolean success = false;
//  private T data;
//  private String code;
//  private String message;
//
//  public HttpRestResult() {}
//
//  public HttpRestResult(boolean success, T data, String code, String message) {
//    this.success = success;
//    this.data = data;
//    this.code = code;
//    this.message = message;
//  }
//
//  public boolean isSuccess() {
//    return success;
//  }
//
//  public void setSuccess(boolean success) {
//    this.success = success;
//  }
//
//  public T getData() {
//    return data;
//  }
//
//  public void setData(T data) {
//    this.data = data;
//  }
//
//  public String getCode() {
//    return code;
//  }
//
//  public void setCode(String code) {
//    this.code = code;
//  }
//
//  public String getMessage() {
//    return message;
//  }
//
//  public void setMessage(String message) {
//    this.message = message;
//  }
//}
