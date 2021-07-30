package com.coco.framework.cocobizlog.mesh.api;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.coco.framework.cocobizlog.mesh.factory.OkHttpFactory;
import com.coco.framework.cocobizlog.bean.LogEntity;
import com.coco.framework.cocobizlog.config.CocoHostProfileEnvJudge;
import com.coco.terminal.cocobizlog.bean.HttpRestResult;
import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import com.coco.terminal.cocobizlog.bean.LogEntitySearchDTO;
import com.coco.terminal.cocobizlog.bean.PagingResult;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.util.StringUtils;

/**
 * okhttp3 连接
 *
 * @author ckli01
 * @date 2019-04-01
 */
@Slf4j
public class OkHttpRemoteApi implements CocoBizLogRemoteApi {

  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  /**
   * 最多请求次数
   */
  private static final int MAX_REQUEST_TIMES = 3;

  /**
   * 添加
   *
   * @param logEntity
   * @return
   */
  @Override
  public HttpRestResult<Boolean> add(LogEntity logEntity) {
    RequestBody body = RequestBody.create(JSON, JSONObject.toJSONString(logEntity));
    return addRequest(body, 0);
  }

  @Override
  public PagingResult<LogEntityDO> search(LogEntitySearchDTO logEntitySearchDTO) {
    RequestBody body = RequestBody.create(JSON, JSONObject.toJSONString(logEntitySearchDTO));
    return searchRequest(body, 0);
  }

  @Override
  public HttpRestResult<Boolean> initIndex(List<LogEntityDO> list) {
    RequestBody body = RequestBody.create(JSON, JSONObject.toJSONString(list));
    return initIndexRequest(body, 0);
  }


  /**
   * 获取 添加请求Url
   *
   * @return
   */
  private String getAddUrl() {
    return String.format("http://%s/cocoBizLog", CocoHostProfileEnvJudge.getHost());
  }

  /**
   * 获取 查询请求Url
   *
   * @return
   */
  private String getSearchUrl() {
    return String.format("http://%s/cocoBizLogSearch", CocoHostProfileEnvJudge.getHost());
  }

  /**
   * 获取 查询请求Url
   *
   * @return
   */
  private String getInitIndexUrl() {
    return String.format("http://%s/cocoBizLog/initIndex", CocoHostProfileEnvJudge.getHost());
  }

  /**
   * 发送请求方法
   *
   * @param body
   * @param i
   * @return
   */
  private HttpRestResult<Boolean> addRequest(RequestBody body, int i) {
    if (i < MAX_REQUEST_TIMES) {
      Request request = new Request.Builder().url(getAddUrl()).post(body).build();
      try {

        Response response = OkHttpFactory.getClient().newCall(request).execute();

        String result = response.body() != null ? response.body().string() : null;
        if (!StringUtils.isEmpty(result)) {
          return JSONObject.parseObject(result, new TypeReference<HttpRestResult<Boolean>>() {
          });
        }

      } catch (IOException e) {
        log.warn("OkHttpRemoteApi add error for {}, times : {}", e.getMessage(), i);
        addRequest(body, ++i);
      }
    }

    return new HttpRestResult<>();
  }

  /**
   * 发送请求方法
   *
   * @param body
   * @param i
   * @return
   */
  private PagingResult<LogEntityDO> searchRequest(RequestBody body, int i) {
    if (i < MAX_REQUEST_TIMES) {
      Request request = new Request.Builder().url(getSearchUrl()).post(body).build();
      try {

        Response response = OkHttpFactory.getClient().newCall(request).execute();

        String result = response.body() != null ? response.body().string() : null;
        if (!StringUtils.isEmpty(result)) {
          return JSONObject.parseObject(
              result, new TypeReference<PagingResult<LogEntityDO>>() {
              });
        }

      } catch (IOException e) {
        log.warn("OkHttpRemoteApi add error for {}, times : {}", e.getMessage(), i);
        searchRequest(body, ++i);
      }
    }

    return new PagingResult<>();
  }


  /**
   * 初始化 索引
   *
   * @param body
   * @param i
   * @return
   */
  private HttpRestResult<Boolean> initIndexRequest(RequestBody body, int i) {
    if (i < MAX_REQUEST_TIMES) {
      Request request = new Request.Builder().url(getInitIndexUrl()).post(body).build();
      try {
        Response response = OkHttpFactory.getClient().newCall(request).execute();

        String result = response.body() != null ? response.body().string() : null;
        if (!StringUtils.isEmpty(result)) {
          return JSONObject.parseObject(result, new TypeReference<HttpRestResult<Boolean>>() {
          });
        }

      } catch (IOException e) {
        log.warn("OkHttpRemoteApi initIndexRequest error for {}, times : {}", e.getMessage(), i);
        addRequest(body, ++i);
      }
    }
    return new HttpRestResult<>();

  }
}
