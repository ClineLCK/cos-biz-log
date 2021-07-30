package com.coco.terminal.cocobizlog.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.coco.terminal.cocobizlog.bean.HttpRestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Map;

/**
 * restTemplate Rest 工具类
 *
 * @author ckli01
 * @date 2018/7/4
 */
@Slf4j
public class RestUtils {


    /**
     * get 请求返回体
     * <p>
     * rest: false
     * http://localhost:8080/test/msmss?phone=1&msg=2     拼接为普通http请求
     * rest: true
     * http://localhost:8080/test/msmss/{id}?hihi={hihi}     map      rest请求
     *
     * @param url  请求路由
     * @param map  请求参数
     * @param rest 表名请求url是否为restful
     * @return
     * @throws Exception
     */
    private static ResponseEntity<String> getResponseEntity(String url, Map<String, Object> map, boolean rest) throws Exception {
        RestTemplate restTemplate = getRestTemplate();
        ResponseEntity<String> result;

        //restful 请求
        if (rest) {
            if (CollectionUtils.isEmpty(map)) {
                result = restTemplate.getForEntity(url, String.class);
            } else {
                result = restTemplate.getForEntity(url, String.class, map);
            }
        } else {
            //普通http请求
            if (CollectionUtils.isEmpty(map)) {
                result = restTemplate.getForEntity(url, String.class);
            } else {
                // restTemplate 弱点
                StringBuilder stringBuilder = new StringBuilder(url);
                stringBuilder.append("?");
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    // 集合元素处理
                    if (entry.getValue() instanceof Collection) {
                        for (Object object : (Collection) entry.getValue()) {
                            addPath(stringBuilder, entry.getKey(), object);
                        }
                    } else {
                        addPath(stringBuilder, entry.getKey(), entry.getValue());
                    }
                }
                // 去除最后一位&
                result = restTemplate.getForEntity(stringBuilder.substring(0, stringBuilder.length() - 1), String.class);
            }
        }
        return result;
    }

    /**
     * get 请求拼装
     *
     * @param stringBuilder
     * @param key
     * @param value
     * @return
     */
    private static void addPath(StringBuilder stringBuilder, String key, Object value) {
        stringBuilder.append(key);
        stringBuilder.append("=");
        stringBuilder.append(value);
        stringBuilder.append("&");
    }


    /**
     * 直接返回rest 请求内容
     *
     * @param url    请求地址
     * @param result 请求返回内容
     * @return
     */
    private static String resultResponse(String url, ResponseEntity<String> result) {
        log.info("rest request for url:{} get responseCode: {}", url, result.getStatusCode().toString());
        if (HttpStatus.OK.equals(result.getStatusCode())) {
            return result.getBody();
        } else {
            log.error("rest request may got a wrong response for: {} ", result.getBody());
            return "";
        }
    }

    /**
     * rest get 请求返回String 请求体
     *
     * @param url 请求路由
     * @param map 请求参数
     * @return
     */
    public static String get(String url, Map<String, Object> map, boolean rest) {
        try {
            if (StringUtils.isEmpty(url)) {
                log.error("rest request url can't be null or empty");
                return "";
            }
            ResponseEntity<String> result = getResponseEntity(url, map, rest);
            return resultResponse(url, result);
        } catch (Exception e) {
            log.error("{} for url: {}", e.getMessage(), url, e);
            return "";
        }
    }


    /**
     * get 请求 返回 HttpRestResult 封装体
     *
     * @param url 请求路由
     * @param map 请求参数
     * @return
     */
    public static <T> HttpRestResult<T> getForHttpRestResult(String url, Map<String, Object> map, boolean rest) {
        try {
            if (StringUtils.isEmpty(url)) {
                return new HttpRestResult<>(false, null, "", "url can't be null or empty");
            }
            ResponseEntity<String> result = getResponseEntity(url, map, rest);
            return resultEntityResponse(url, result);
        } catch (Exception e) {
            log.error("{} for url: {}", e.getMessage(), url, e);
            return new HttpRestResult<>(false, null, "", e.getMessage());
        }
    }


    /**
     * 构造返回结构体
     *
     * <p>
     * // todo 不支持 T -> List<T>   List 中T 为 JSONObject
     * // GSON 获取的是HashMap
     * // JackSon 获取的是LinkedHashMap
     *
     * @param url    请求地址
     * @param result 请求返回内容
     * @param <T>    请求封装转换类
     * @return
     */
    private static <T> HttpRestResult<T> resultEntityResponse(String url, ResponseEntity<String> result) {
        log.info("rest request for url:{} get responseCode: {}", url, result.getStatusCode().toString());
        if (HttpStatus.OK.equals(result.getStatusCode())) {
            return JSONObject.parseObject(result.getBody(), new TypeReference<HttpRestResult<T>>() {
            });
        } else {
            log.error("rest request may got a wrong response for: {} ", result.getBody());
            return new HttpRestResult<>(false, null, result.getStatusCode().toString(), result.getBody());
        }
    }

    /**
     * post 请求返回体
     *
     * @param url
     * @param object
     * @return
     * @throws Exception
     */
    private static ResponseEntity<String> postResponseEntity(String url, Object object) throws Exception {
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders httpHeaders = getHeaders();

        String requestBody = "";
        if (object != null) {
            requestBody = JSONObject.toJSONString(object);
        }
        //利用容器实现数据封装，发送
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);

        return restTemplate.postForEntity(url, entity, String.class);
    }


    /**
     * POST 请求返回封装HttpRestResult
     *
     * @param url    请求路由
     * @param object 实体
     * @param <T>    返回类型实体类
     * @return
     */
    public static <T> HttpRestResult<T> postForHttpRestResult(String url, Object object) {
        try {
            if (StringUtils.isEmpty(url)) {
                return new HttpRestResult<>(false, null, "", "url can't be null or empty");
            }
            ResponseEntity<String> result = postResponseEntity(url, object);
            return resultEntityResponse(url, result);
        } catch (Exception e) {
            log.error("{} for url: {}", e.getMessage(), url, e);
            return new HttpRestResult<>(false, null, "", e.getMessage());
        }
    }

    /**
     * rest post 请求直接返回
     *
     * @param url
     * @param object
     * @return
     */
    public static String post(String url, Object object) {
        try {
            if (StringUtils.isEmpty(url)) {
                log.error("rest request url can't be null or empty");
                return "";
            }
            ResponseEntity<String> result = postResponseEntity(url, object);
            return resultResponse(url, result);
        } catch (Exception e) {
            log.error("{} for url: {}", e.getMessage(), url, e);
            return "";
        }
    }


    /**
     * rest delete 请求 无返回
     *
     * @param url
     * @param map
     */
    public static void delete(String url, Map<String, Object> map) {
        try {
            if (StringUtils.isEmpty(url)) {
                log.error("rest request url can't be null or empty");
            } else {
                RestTemplate restTemplate = getRestTemplate();

                if (CollectionUtils.isEmpty(map)) {
                    restTemplate.delete(url);
                } else {
                    restTemplate.delete(url, map);
                }
            }
        } catch (Exception e) {
            log.error("{} for url: {}", e.getMessage(), url, e);
        }
    }

    /**
     * delete 请求返回封装HttpRestResult
     *
     * @param url 请求路由
     * @param map 请求参数
     * @param <T> 返回类型实体类
     * @return
     */
    public static <T> HttpRestResult<T> deleteForHttpRestResult(String url, Map<String, Object> map) {
        try {
            if (StringUtils.isEmpty(url)) {
                return new HttpRestResult<>(false, null, "", "url can't be null or empty");
            }
            ResponseEntity<String> result = deleteResponseEntity(url, map);
            return resultEntityResponse(url, result);
        } catch (Exception e) {
            log.error("{} for url: {}", e.getMessage(), url, e);
            return new HttpRestResult<>(false, null, "", e.getMessage());
        }
    }

    /**
     * delete 请求返回体
     * 只支持restful 格式
     * 即 http://localhost:8080/test/msmss/{id}?hihi={hihi}     map      rest请求
     *
     * @param url
     * @param map
     * @return
     */
    private static ResponseEntity<String> deleteResponseEntity(String url, Map<String, Object> map) {
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders httpHeaders = getHeaders();

        //利用容器实现数据封装，发送
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        if (CollectionUtils.isEmpty(map)) {
            return restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
        } else {
            return restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class, map);
        }
    }


    /**
     * 获取restTemplate 实例
     *
     * @return
     */
    private static RestTemplate getRestTemplate() {
        return CocoSpringContext.getBean(RestTemplate.class);
    }

    /**
     * 设置HTTP请求头信息，实现编码,返回接受类型等
     *
     * @return
     */
    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        return headers;
    }


}
