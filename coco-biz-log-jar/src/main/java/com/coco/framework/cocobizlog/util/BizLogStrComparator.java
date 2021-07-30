package com.coco.framework.cocobizlog.util;

import com.coco.framework.cocobizlog.core.annotation.BizLogVsClass;
import com.coco.framework.cocobizlog.core.annotation.BizLogVsField;
import com.coco.framework.cocobizlog.core.enums.LogEventFieldTypeEnum;
import com.coco.framework.cocobizlog.bean.FieldDTO;
import com.coco.framework.cocobizlog.bean.LogEventFieldEntity;
import com.coco.framework.cocobizlog.common.CosBizLogConstant;
import com.coco.framework.cocobizlog.core.BizLogStr;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * BizlogStr 比较器
 *
 * @author ckli01
 * @date 2019-04-18
 */
@Slf4j
public class BizLogStrComparator {

    /**
     * 对象比较，如果需要自定义比较方式，必须子类实现 规则： 1、若比较类含有 BizLogVsClass 注解，比较所有属性 2、若没有，则只比较属性上有 BizLogVsField 注解
     *
     * @param oldValue 旧值
     * @param newValue 新值
     * @return
     */
    public static List<LogEventFieldEntity> compareFields(
            Object oldValue, Object newValue, FieldDTO fieldDTO) {

        List<LogEventFieldEntity> fieldEntities = new ArrayList<>();

        if (null != newValue) {
            Class<?> clazz = newValue.getClass();
            Annotation bizLogVsClass = clazz.getAnnotation(BizLogVsClass.class);
            Field[] fields = clazz.getDeclaredFields();

            boolean flag = false;
            if (null != bizLogVsClass) {
                flag = true;
            }

            for (Field field : fields) {
                try {
                    // 若类上没有注解 则以属性上注解为依据
                    if (!flag) {
                        BizLogVsField bizLogVsField = field.getAnnotation(BizLogVsField.class);
                        if (null == bizLogVsField) {
                            continue;
                        }
                    }
                    List<LogEventFieldEntity> result =
                            compareSingleField(field, oldValue, newValue, fieldDTO);

                    if (!CollectionUtils.isEmpty(result)) {
                        fieldEntities.addAll(result);
                    }

                } catch (Exception e) {
                    log.error(
                            "compareSingleField for class: {} filed: {} error: {}",
                            clazz.getName(),
                            field.getName(),
                            e.getMessage(),
                            e);
                }
            }
        }
        return fieldEntities;
    }

    /**
     * 获取属性值
     *
     * @param clazz
     * @param fieldName
     * @param obj
     * @return
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     */
    public static Object fieldValue(Class<?> clazz, String fieldName, Object obj) throws Exception {
        if (clazz == null) {
            return null;
        }
        Field realField = clazz.getDeclaredField(fieldName);

        realField.setAccessible(true);
        Object val = realField.get(obj);
        if (null == val) {
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);
            // 获得get方法
            Method getMethod = pd.getReadMethod();
            if (null != getMethod) {
                val = getMethod.invoke(obj);
            }
        }
        return val;
    }

    /**
     * 比较相同类相同属性值 规则： 1、先取值，若值相同则不比较 2、取注解 BizLogVsField 标示日志显示别名以及别名取值，若没有给定别名或者别名取值方法，则使用属性名称，以及属性对应getter方法
     * 3、校验是否是BizLogStr 子类，子类相比较可以调用子类compareFields方法比较两个对象
     *
     * @param field    比较属性名
     * @param oldValue 旧值
     * @param newValue 新值
     * @return
     * @throws Exception
     */
    private static List<LogEventFieldEntity> compareSingleField(
            Field field, Object oldValue, Object newValue, FieldDTO fieldDTO) throws Exception {

        List<LogEventFieldEntity> list = new ArrayList<>();

        Method method = null;
        // 中文显示名称
        String fieldNameZh = addPrefixToFieldNameZh(fieldDTO.getFieldNameZh(), field.getName(),
                fieldDTO);
        // 英文显示字段名称
        String fieldNameEn = addPrefixToFieldNameEn(fieldDTO.getFieldNameEn(), field.getName(),
                fieldDTO);
        // 旧值
        Object oldReturnValue =
                fieldValue(oldValue == null ? null : oldValue.getClass(), field.getName(), oldValue);
        // 新值
        Object newReturnValue =
                fieldValue(newValue == null ? null : newValue.getClass(), field.getName(), newValue);

        BizLogVsField bizLogVsField = field.getAnnotation(BizLogVsField.class);

        // 判断是否相同，若相同返回，不同取别名
        if (compareField(oldReturnValue, newReturnValue)) {
            return list;
        }

        if (null != bizLogVsField) {
            // 若配置别名，使用属性别名做日志记录
            if (!StringUtils.isEmpty(bizLogVsField.fieldNameStr())) {
                fieldNameZh = addPrefixToFieldNameZh(fieldDTO.getFieldNameZh(),
                        bizLogVsField.fieldNameStr(),
                        fieldDTO);
            }
            // 优先根据注解根据method名称 取值
            if (!StringUtils.isEmpty(bizLogVsField.strMethodName())) {
                method = newValue.getClass().getMethod(bizLogVsField.strMethodName());
                // 获取别名属性值
                if (null != method) {
                    log.debug(
                            "compareSingleField for class: {} method: {}",
                            newValue.getClass().getName(),
                            method.getName());
                    oldReturnValue = method.invoke(oldValue);
                    newReturnValue = method.invoke(newValue);
                }
            }
        }

        // 判断方法的返回类型
        Class<?> fieldType = field.getType();
        if (BizLogStr.class.isAssignableFrom(fieldType)) {
            FieldDTO tran = new FieldDTO();
            tran.setFieldNameEn(fieldNameEn);
            tran.setFieldNameZh(fieldNameZh);
            tran.setPrefixForList(fieldDTO.getPrefixForList());
            tran.setKeyPrefixWithPoint(bizLogVsField != null && bizLogVsField.childExtend());
            // 子类 是 对象 的进行 比较
            childObjectCompare(oldReturnValue, newReturnValue, list, tran, fieldType);
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            FieldDTO tran = new FieldDTO();
            tran.setFieldNameEn(fieldNameEn);
            tran.setFieldNameZh(fieldNameZh);
            tran.setPrefixForList(true);
            tran.setKeyPrefixWithPoint(bizLogVsField != null && bizLogVsField.childExtend());
            // 子类 是 列表 的进行 比较
            childCollectionCompare(oldReturnValue, newReturnValue, list, tran, field, oldValue, newValue);
        } else {
            // 封装 比较内容 ，如果是List  的 话，值前面添加前缀
            if (fieldDTO.getPrefixForList()) {
                oldReturnValue = addZhPrefixToFieldName(oldReturnValue, oldValue);
                newReturnValue = addZhPrefixToFieldName(newReturnValue, newValue);
            }

            StringBuilder result = formaterChangeStr(oldReturnValue, newReturnValue);

            LogEventFieldEntity logEventFieldEntity = new LogEventFieldEntity();
            logEventFieldEntity.setLogEventFieldTypeEnum(LogEventFieldTypeEnum.BODY);
            logEventFieldEntity.setFiledName(fieldNameEn);
            logEventFieldEntity.setFiledNameZh(fieldNameZh);
            logEventFieldEntity.setResult(result.toString());

            list.add(logEventFieldEntity);
            log.debug(
                    "compareSingleField for class: {} filed: {} result: {}",
                    newValue.getClass().getName(),
                    field.getName(),
                    result);
        }
        return list;
    }


    /**
     * 对象子类 比较
     *
     * @param oldReturnValue
     * @param newReturnValue
     * @param list
     * @param fieldDTO
     * @param fieldType
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static void childObjectCompare(
            Object oldReturnValue,
            Object newReturnValue,
            List<LogEventFieldEntity> list,
            FieldDTO fieldDTO,
            Class fieldType)
            throws IllegalAccessException, InstantiationException {
        // 防止nullPointException
        if (null == oldReturnValue) {
            oldReturnValue = fieldType.newInstance();
        }
        if (null == newReturnValue) {
            newReturnValue = fieldType.newInstance();
        }
        // 调用compareFields 循环
        List<LogEventFieldEntity> sonResult = compareFields(oldReturnValue, newReturnValue, fieldDTO);

        if (!CollectionUtils.isEmpty(sonResult)) {
            list.addAll(sonResult);
        }
    }

    /**
     * 子类 列表 比较
     *
     * @param oldReturnValue
     * @param newReturnValue
     * @param list
     * @param fieldDTO
     * @param field
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private static void childCollectionCompare(
            Object oldReturnValue,
            Object newReturnValue,
            List<LogEventFieldEntity> list,
            FieldDTO fieldDTO,
            Field field,
            Object oldValue,
            Object newValue)
            throws InstantiationException, IllegalAccessException {
        // 获取 Collection 范型类型
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        if (parameterizedType.getActualTypeArguments().length > 0) {
            Type type = parameterizedType.getActualTypeArguments()[0];
            Class clazz = (Class) type;
            // 根据 主键唯一区分
            if (BizLogStr.class.isAssignableFrom(clazz)) {
                Map<Object, Object> oldMap = collectionToMap((Collection) oldReturnValue);
                Map<Object, Object> newMap = collectionToMap((Collection) newReturnValue);
                Set<Object> keys = new HashSet<>();
                keys.addAll(oldMap.keySet());
                keys.addAll(newMap.keySet());

                for (Object key : keys) {
                    Object oldColObj = oldMap.get(key);
                    Object newColObj = newMap.get(key);

                    childObjectCompare(oldColObj, newColObj, list, fieldDTO, clazz);
                }
            } else {
                // String Integer.. 常用类型
                LogEventFieldEntity logEventFieldEntity = new LogEventFieldEntity();

                oldReturnValue = addZhPrefixToFieldName(oldReturnValue, oldValue);
                newReturnValue = addZhPrefixToFieldName(newReturnValue, newValue);

                logEventFieldEntity.setFiledName(fieldDTO.getFieldNameEn());
                logEventFieldEntity.setFiledNameZh(fieldDTO.getFieldNameZh());
                logEventFieldEntity.setLogEventFieldTypeEnum(LogEventFieldTypeEnum.BODY);
                StringBuilder result = formaterChangeStr(oldReturnValue, newReturnValue);
                logEventFieldEntity.setResult(result.toString());
                list.add(logEventFieldEntity);
            }
        }
    }

    /**
     * 给字段添加 前缀 中文描述
     *
     * @param prefix
     * @param fileName
     * @param fieldDTO
     * @return
     */
    private static String addPrefixToFieldNameZh(String prefix, String fileName,
                                                 FieldDTO fieldDTO) {
        if (fieldDTO.getKeyPrefixWithPoint()) {
            return StringUtils.isEmpty(prefix) ? fileName : prefix + "." + fileName;
        } else {
            return fileName;
        }
    }

    /**
     * 给字段添加 前缀 字段名
     *
     * @param prefix
     * @param fileName
     * @param fieldDTO
     * @return
     */
    private static String addPrefixToFieldNameEn(String prefix, String fileName,
                                                 FieldDTO fieldDTO) {
        return StringUtils.isEmpty(prefix) ? fileName : prefix + "." + fileName;
    }

    /**
     * 给中文字段添加 前缀
     *
     * @param oldReturnValue
     * @param value
     * @return
     */
    private static Object addZhPrefixToFieldName(Object oldReturnValue, Object value) {
        if (oldReturnValue != null
                && value != null
                && !StringUtils.isEmpty(((BizLogStr) value).cocoKey())) {
            return CosBizLogConstant.CHILDREN_FIRST_SPLIT + ((BizLogStr) value).cocoKey() + CosBizLogConstant.CHILDREN_LAST_SPLIT + oldReturnValue;
        }
        return oldReturnValue;
    }

    /**
     * 转换比较类型结果为字符串输出
     *
     * @param oldValue
     * @param newValue
     * @return
     */
    private static StringBuilder formaterChangeStr(Object oldValue, Object newValue) {

        return new StringBuilder()
                .append(oldValue == null ? "" : oldValue)
                .append(" ")
                .append(CosBizLogConstant.VALUE_SPLIT)
                .append(" ")
                .append(newValue == null ? "" : newValue);
    }

    /**
     * 基本类型比较，如String、Long、Integer....
     *
     * @param o1
     * @param o2
     * @return
     */
    private static boolean compareField(Object o1, Object o2) {
        boolean flag;
        if (o1 instanceof Collection || o2 instanceof Collection) {
            // Set List Queue
            flag =
                    CollectionUtils.isEmpty((Collection<?>) o1)
                            && CollectionUtils.isEmpty((Collection<?>) o2);
        } else if (o1 instanceof Map || o2 instanceof Map) {
            flag = CollectionUtils.isEmpty((Map<?, ?>) o1) && CollectionUtils.isEmpty((Map<?, ?>) o2);
        } else {
            flag = StringUtils.isEmpty(o1) && StringUtils.isEmpty(o2);
        }

        if (flag || null != o1 && o1.equals(o2)) {
            return true;
        }
        return false;
    }

    private static Map<Object, Object> collectionToMap(Collection collection) {
        Map<Object, Object> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(collection)) {
            for (Object object : collection) {
                BizLogStr bizLogStr = (BizLogStr) object;
                map.put(bizLogStr.cocoKey(), bizLogStr);
            }
        }
        return map;
    }
}
