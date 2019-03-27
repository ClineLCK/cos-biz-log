package com.nfsq.framework.cosbizlog.bean;


import com.nfsq.framework.cosbizlog.annotation.BizLogVsClass;
import com.nfsq.framework.cosbizlog.annotation.BizLogVsField;
import com.nfsq.framework.cosbizlog.aop.BizLogAop;
import com.nfsq.framework.cosbizlog.util.CosBizLogConstant;
import com.nfsq.framework.cosbizlog.util.WrappedBeanCopier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 日志服务 实体 功能类
 * 需要写日志服务且需要自定义日志实体内容的类，重写prefixBizLogStr()
 *
 * @author ckli01
 * @date 2018/9/3
 */
@Slf4j
public abstract class BizLogStr {


    /**
     * 获取主键Id
     *
     * @return
     */
    public abstract Object getId();

    /**
     * 获取自定义日志取值,前缀
     *
     * @return
     */
    public String prefixBizLogStr() {
        return getId() != null ? "id: " + getId().toString() + CosBizLogConstant.MARK_NEW_LINE : CosBizLogConstant.EMPTY;
    }

    /**
     * 获取自定义日志取值,后缀
     *
     * @return
     */
    public String suffixBizLogStr() {
        return CosBizLogConstant.EMPTY;
    }


    /**
     * 转换类型
     *
     * @param clazz
     * @return
     */
    public Object convertDoToVo(Class<?> clazz) {
        return WrappedBeanCopier.copyProperties(this, clazz);
    }

    /**
     * 对象比较，如果需要自定义比较方式，必须子类实现
     * 规则：
     * 1、若比较类含有 BizLogVsClass  注解，比较所有属性
     * 2、若没有，则只比较属性上有 BizLogVsField 注解
     *
     * @param o
     * @return
     */
    public String compareFields(Object o) {
        StringBuilder stringBuilder = new StringBuilder();

        if (null != o) {
            // 校验是同一个类
            if (o instanceof BizLogStr && this.getClass().equals(o.getClass())) {
                Class<?> clazz = this.getClass();
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
                        StringBuilder result = compareSingleField(clazz, field, this, o);
                        if(!StringUtils.isEmpty(result)){
                            BizLogAop.entityJsonThreadLocal.get().put(field.getName(),result);
                            stringBuilder.append(result);
                        }

                    } catch (Exception e) {
                        log.error("compareSingleField for class: {} filed: {} error: {}", clazz.getName(), field.getName(), e.getMessage(), e);
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 获取属性值
     *
     * @param clazz
     * @param field
     * @param obj
     * @return
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     */
    private Object fieldValue(Class<?> clazz, Field field, Object obj) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        field.setAccessible(true);
        Object val = field.get(obj);

        if (null == val) {
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
            //获得get方法
            Method getMethod = pd.getReadMethod();
            if (null != getMethod) {
                val = getMethod.invoke(obj);
            }
        }
        return val;
    }

    /**
     * 比较相同类相同属性值
     * 规则：
     * 1、先取值，若值相同则不比较
     * 2、取注解   BizLogVsField 标示日志显示别名以及别名取值，若没有给定别名或者别名取值方法，则使用属性名称，以及属性对应getter方法
     * 3、校验是否是BizLogStr 子类，子类相比较可以调用子类compareFields方法比较两个对象
     *
     * @param clazz
     * @param field
     * @param oldValue
     * @param newValue
     * @return
     * @throws Exception
     */
    private StringBuilder compareSingleField(Class<?> clazz, Field field, Object oldValue, Object newValue) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Method method = null;
        String fieldName = field.getName();

        Object oldReturnValue = fieldValue(clazz, field, oldValue);
        Object newReturnValue = fieldValue(clazz, field, newValue);

        // 判断是否相同，若相同返回，不同取别名
        if (compareField(oldReturnValue, newReturnValue)) {
            return stringBuilder;
        }

        BizLogVsField bizLogVsField = field.getAnnotation(BizLogVsField.class);
        if (null != bizLogVsField) {
            // 若配置别名，使用属性别名做日志记录
            if (!StringUtils.isEmpty(bizLogVsField.fieldNameStr())) {
                fieldName = bizLogVsField.fieldNameStr();
            }
            // 优先根据注解根据method名称 取值
            if (!StringUtils.isEmpty(bizLogVsField.strMethodName())) {
                method = clazz.getMethod(bizLogVsField.strMethodName());
                // 获取别名属性值
                if (null != method) {
                    log.debug("compareSingleField for class: {} method: {}", clazz.getName(), method.getName());
                    oldReturnValue = method.invoke(oldValue);
                    newReturnValue = method.invoke(newValue);
                }
            }
        }

        // 判断方法的返回类型 是否是BizLogStr 子类
        Class<?> fieldType = field.getType();
        if (BizLogStr.class.isAssignableFrom(fieldType)) {
            // 防止nullPointException
            if (null == oldReturnValue) {
                oldReturnValue = fieldType.newInstance();
            }
            if (null == newReturnValue) {
                newReturnValue = fieldType.newInstance();
            }
            // 调用compareFields 循环
            BizLogStr bizLogStr = (BizLogStr) oldReturnValue;
            stringBuilder.append(bizLogStr.compareFields(newReturnValue));
        } else {
            stringBuilder.append(formaterChangeStr(fieldName, oldReturnValue, newReturnValue));
        }
        log.debug("compareSingleField for class: {} filed: {} result: {}", clazz.getName(), field.getName(), stringBuilder.toString());
        return stringBuilder;

    }


    /**
     * 转换比较类型结果为字符串输出
     *
     * @param label
     * @param oldValue
     * @param newValue
     * @return
     */
    protected StringBuilder formaterChangeStr(String label, Object oldValue, Object newValue) {

        return new StringBuilder().append(label)
                .append(": ")
                .append(oldValue)
                .append(" -> ")
                .append(newValue)
                .append(CosBizLogConstant.MARK_NEW_LINE);
    }

    /**
     * 基本类型比较，如String、Long、Integer....
     *
     * @param o1
     * @param o2
     * @return
     */
    protected boolean compareField(Object o1, Object o2) {
        boolean flag;
        if (o1 instanceof Collection || o2 instanceof Collection) {
            // Set List Queue
            flag = CollectionUtils.isEmpty((Collection<?>) o1) && CollectionUtils.isEmpty((Collection<?>) o2);
            if (!flag && o1 instanceof List || o2 instanceof List) {
                Collections.sort((List) o1);
                Collections.sort((List) o2);
            }
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
}
