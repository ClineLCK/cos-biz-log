package com.coco.framework.cocobizlog.mesh.factory;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

/**
 * Okhttp 3 工厂
 *
 * @author ckli01
 * @date 2019-04-01
 */
public class OkHttpFactory {

  /** 连接超时时间 */
  private static final int OKHTTP_CONNECT_TIMEOUT = 3;
  /** 读超时时间 */
  private static final int OKHTTP_READ_TIMEOUT = 3;

  private static volatile OkHttpClient okHttpClient;

  public static OkHttpClient getClient() {

    if (null != okHttpClient) {
      return okHttpClient;
    } else {
      synchronized (OkHttpFactory.class) {
        if (null != okHttpClient) {
          return okHttpClient;
        } else {
          okHttpClient =
              new OkHttpClient.Builder()
                  .connectTimeout(OKHTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                  .readTimeout(OKHTTP_READ_TIMEOUT, TimeUnit.SECONDS)
                  //                            .retryOnConnectionFailure(true)
                  //                            .addInterceptor(new RetryIntercepter())
                  .build();
          return okHttpClient;
        }
      }
    }
  }
}
