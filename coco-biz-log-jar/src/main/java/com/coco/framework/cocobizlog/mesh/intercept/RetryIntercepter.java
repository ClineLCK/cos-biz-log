package com.coco.framework.cocobizlog.mesh.intercept;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * okhttp 拦截器
 *
 * @author ckli01
 * @date 2019-04-01
 */
@Slf4j
public class RetryIntercepter implements Interceptor {

  /** 最大重试次数 */
  private int maxRetry;
  /** 连接次数 */
  private int retryNum = 0;

  public RetryIntercepter() {
    this.maxRetry = 3;
  }

  public RetryIntercepter(int maxRetry) {
    this.maxRetry = maxRetry;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Response response = chain.proceed(request);
    while (!response.isSuccessful() && retryNum < maxRetry) {
      retryNum++;
      response = chain.proceed(request);
    }
    return response;
  }
}
