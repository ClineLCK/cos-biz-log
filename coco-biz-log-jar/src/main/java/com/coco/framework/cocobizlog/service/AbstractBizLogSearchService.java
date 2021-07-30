package com.coco.framework.cocobizlog.service;

import com.coco.framework.cocobizlog.core.annotation.BizLogVsClass;
import com.coco.framework.cocobizlog.core.annotation.BizLogVsField;
import com.coco.framework.cocobizlog.mesh.api.OkHttpRemoteApi;
import com.coco.framework.cocobizlog.common.CosBizLogConstant;
import com.coco.framework.cocobizlog.core.BizLogStr;
import com.coco.framework.cocobizlog.util.CosBizLogSpringContext;
import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import com.coco.terminal.cocobizlog.bean.LogEntitySearchDTO;
import com.coco.terminal.cocobizlog.bean.PagingResult;
import com.coco.terminal.cocobizlog.constants.CocoBizLogConstant;
import com.coco.terminal.cocobizlog.enums.LogFieldQueryTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 日志查询 抽象类
 *
 * @author ckli01
 * @date 2019-09-18
 */
@Slf4j
public abstract class AbstractBizLogSearchService {

    /**
     * 自定义前后缀信息 匹配 默认实现 需要 每个key 前面添加 CosBizLogConstant.KEY_PREFIX_STR
     *
     * @param obj
     * @return
     */
    protected abstract List<String> prefixSuffix(Object obj);

    /**
     * 自定义 比较类 记录 属性
     *
     * @param map
     * @return
     */
    protected Map<String, Object> userDefine(Map<String, String> map) {
        Map<String, Object> userDefineMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(map)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (StringUtils.isEmpty(entry.getValue())) {
                    userDefineMap.put(CocoBizLogConstant.USER_DEFINE + "." + entry.getKey(), "");
                } else {
                    userDefineMap.put(
                            CocoBizLogConstant.USER_DEFINE + "." + entry.getKey(), entry.getValue());
                }
            }
        }
        return userDefineMap;
    }

    /**
     * 查询
     *
     * @param logEntitySearchDTO
     * @param obj
     * @return
     */
    public PagingResult<LogEntityDO> search(
            LogEntitySearchDTO logEntitySearchDTO, Object obj, Map<String, String> map) {
        Objects.requireNonNull(logEntitySearchDTO.getBaseSearchDTO());

        logEntitySearchDTO.setPrefixSuffix(prefixSuffix(obj));

        logEntitySearchDTO.setEntityJson(userDefine(map));

        if (!CollectionUtils.isEmpty(logEntitySearchDTO.getBaseSearchDTO().getSearchMap())) {
            Map<String, Object> map1 = new HashMap<>();
            logEntitySearchDTO.getBaseSearchDTO().getSearchMap().forEach((k, v) -> map1.put(CosBizLogConstant.SEARCH_FIELD_PREFIX + k, v));
            logEntitySearchDTO.getBaseSearchDTO().setSearchMap(map1);
        }
        //  查询
        PagingResult<LogEntityDO> httpRestResult =
                CosBizLogSpringContext.getBean(OkHttpRemoteApi.class).search(logEntitySearchDTO);
        if (!httpRestResult.isSuccess()) {
            log.warn("cosBizLog search  cocoBizLog failed  message : {}", httpRestResult.getMessage());
        } else {
            log.info("cosBizLog search  cocoBizLog success");
        }
        return httpRestResult;
    }

    /**
     * 获取 字段 被记录的字段名  es 查询字段
     *
     * @param clazz
     * @return
     */
    public Map<String, String> fieldsMap(Class<? extends BizLogStr> clazz) {
        return staticFieldsMap(clazz, true);
    }


    /**
     * 获取 字段 被记录的字段名 es 查询 反馈 字段转义
     *
     * @param clazz
     * @return
     */
    public Map<String, String> fieldsMapForFieldNameZh(Class<? extends BizLogStr> clazz) {
        return staticFieldsMap(clazz, false);
    }

    /**
     * 将 查询结果 返回的 json 转换为 字符串对应以 逗号 相连的结果 x.x.x - ssss
     *
     * @param map
     * @return
     */
    public Map<String, Object> fieldsChangeMapToStringMap(String prefix, Map<Object, Object> map) {
        Map<String, Object> resultMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(map)) {
            map.forEach((k, v) -> {
                String prefixNext;
                if (!StringUtils.isEmpty(prefix)) {
                    prefixNext = prefix + "." + k;
                } else {
                    prefixNext = (String) k;
                }
                if (v instanceof Map) {
                    resultMap.putAll(fieldsChangeMapToStringMap(prefixNext, (Map<Object, Object>) v));
                } else {
                    resultMap.put(prefixNext, v);
                }
            });
        }
        return resultMap;
    }


    /**
     * 获取 字段 被记录的字段名  es 查询字段
     *
     * @param clazz
     * @return
     */
    public static Map<String, String> staticFieldsMap(Class<? extends BizLogStr> clazz,
                                                      boolean nested) {
        Map<String, String> map = new HashMap<>();

        if (null != clazz) {
            Annotation bizLogVsClass = clazz.getAnnotation(BizLogVsClass.class);
            Field[] fields = clazz.getDeclaredFields();

            boolean flag = false;
            if (null != bizLogVsClass) {
                flag = true;
            }
            for (Field field : fields) {
                try {
                    BizLogVsField bizLogVsField = field.getAnnotation(BizLogVsField.class);
                    // 若类上没有注解 则以属性上注解为依据
                    if (!flag && null == bizLogVsField) {
                        continue;
                    }
                    Class<?> fieldType = field.getType();
                    if (BizLogStr.class.isAssignableFrom(fieldType)) {
                        childField(fieldType, map, bizLogVsField, field.getName(), null, nested);
                    } else if (Collection.class.isAssignableFrom(fieldType)) {
                        // 获取 Collection 范型类型
                        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                        if (parameterizedType.getActualTypeArguments().length > 0) {
                            Type type = parameterizedType.getActualTypeArguments()[0];
                            childField(
                                    (Class) type, map, bizLogVsField, field.getName(), LogFieldQueryTypeEnum.LIST,
                                    nested);
                        }
                    } else {
                        if (null != bizLogVsField && !StringUtils.isEmpty(bizLogVsField.fieldNameStr())) {
                            map.put(field.getName(), bizLogVsField.fieldNameStr());
                        } else {
                            map.put(field.getName(), field.getName());
                        }
                    }

                } catch (Exception e) {
                    log.error(
                            "fieldsMap for class: {} filed: {} error: {}",
                            clazz.getName(),
                            field.getName(),
                            e.getMessage(),
                            e);
                }
            }
        }
        return map;

    }


    /**
     * 获取 孩子 节点 属性 名称
     *
     * @param clazz
     * @param map
     * @param bizLogVsField
     * @param fieldName
     */
    private static void childField(
            Class clazz,
            Map<String, String> map,
            BizLogVsField bizLogVsField,
            String fieldName,
            LogFieldQueryTypeEnum logFieldQueryTypeEnum,
            boolean nested) {
        if (BizLogStr.class.isAssignableFrom(clazz)) {
            Map<String, String> childMap = staticFieldsMap((Class<? extends BizLogStr>) clazz, nested);
            if (!CollectionUtils.isEmpty(childMap)) {
                for (Map.Entry<String, String> entry : childMap.entrySet()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (nested && logFieldQueryTypeEnum != null
                            && !entry.getKey().contains(logFieldQueryTypeEnum.getType())) {
                        stringBuilder.append(logFieldQueryTypeEnum.getType());
                    }

                    stringBuilder.append(fieldName).append(".").append(entry.getKey());

                    if (bizLogVsField.childExtend()) {
                        String fieldNameStr =
                                !StringUtils.isEmpty(bizLogVsField.fieldNameStr())
                                        ? bizLogVsField.fieldNameStr() + "."
                                        : "";
                        map.put(stringBuilder.toString(), fieldNameStr + entry.getValue());
                    } else {
                        map.put(stringBuilder.toString(), entry.getValue());
                    }
                }
            }
        } else {
            if (null != bizLogVsField && !StringUtils.isEmpty(bizLogVsField.fieldNameStr())) {
                map.put(fieldName, bizLogVsField.fieldNameStr());
            } else {
                map.put(fieldName, fieldName);
            }
        }
    }
}
