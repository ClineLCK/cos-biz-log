package com.coco.framework.cocobizlog.core.event.impl;

import com.coco.framework.cocobizlog.bean.FieldDTO;
import com.coco.framework.cocobizlog.bean.LogEventEntity;
import com.coco.framework.cocobizlog.bean.LogEventFieldEntity;
import com.coco.framework.cocobizlog.core.BizLogAop;
import com.coco.framework.cocobizlog.core.BizLogStr;
import com.coco.framework.cocobizlog.core.annotation.BizLogBatchUpdateKey;
import com.coco.framework.cocobizlog.core.event.AbstractEventOperator;
import com.coco.framework.cocobizlog.core.event.EventOperator;
import com.coco.framework.cocobizlog.service.BizLogBaseBizService;
import com.coco.framework.cocobizlog.util.BizLogStrComparator;
import com.coco.framework.cocobizlog.util.CosBizLogSpringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 批量更新 事件 处理
 *
 * @author ckli01
 * @date 2019-05-21
 */
@Slf4j
@Component
public class BatchUpdateEventOperator extends AbstractEventOperator implements EventOperator {

    @Override
    public List<LogEventEntity> operate(Object[] args) {

        List<LogEventEntity> list = new ArrayList<>();
        try {
            if (args[0] != null) {
                Class<?> clazz = BizLogAop.classThreadLocal.get();
                BizLogBaseBizService baseService =
                        (BizLogBaseBizService) CosBizLogSpringContext.getBean(clazz);

                List<Object> ids = new ArrayList<>();
                List<Object> newObjs;

                if (args[0] instanceof List) {
                    newObjs = (List<Object>) args[0];
                    for (Object o : newObjs) {
                        Object id = ((BizLogStr) o).cocoKey();
                        ids.add(id);
                    }

                    List<Object> olds = baseService.getPrefixEntityByIds(ids);

                    Map<String, Object> map = new HashMap<>();

                    if (!CollectionUtils.isEmpty(olds)) {
                        for (Object o : olds) {
                            map.put(((BizLogStr) o).cocoKey().toString(), o);
                        }
                    }

                    // 比较
                    for (Object o : newObjs) {
                        // 获取变更结果
                        LogEventEntity logEventEntity =
                                compareFields(map.get(((BizLogStr) o).cocoKey().toString()), o);

                        logEventEntity.setSearchMap(super.searchMap(o));

                        if (logEventEntity != null) {
                            list.add(logEventEntity);
                        }
                    }

                } else {

                    Object o = args[0];
                    Field[] fields = o.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        BizLogBatchUpdateKey bizLogBatchUpdate =
                                field.getDeclaredAnnotation(BizLogBatchUpdateKey.class);
                        if (bizLogBatchUpdate != null) {

                            field.setAccessible(true);
                            Object val = field.get(o);
                            if (null == val) {
                                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), o.getClass());
                                // 获得get方法
                                Method getMethod = pd.getReadMethod();
                                if (null != getMethod) {
                                    val = getMethod.invoke(o);
                                }
                            }
                            ids.addAll((Collection<?>) val);
                            break;
                        }
                    }

                    if (!CollectionUtils.isEmpty(ids)) {
                        Map<String, Object> newMaps = new HashMap<>();
                        for (Object id : ids) {
                            newMaps.put(id.toString(), o);
                        }

                        List<Object> olds = baseService.getPrefixEntityByIds(ids);

                        if (CollectionUtils.isEmpty(olds)) {
                            newMaps.forEach(
                                    (k, v) -> {
                                        // 获取变更结果
                                        LogEventEntity logEventEntity = compareFields(null, v);
                                        logEventEntity.setSearchMap(super.searchMap(o));
                                        list.add(logEventEntity);
                                    });
                        } else {
                            for (Object obj : olds) {
                                // 获取变更结果
                                LogEventEntity logEventEntity =
                                        compareFields(obj, newMaps.get(((BizLogStr) obj).cocoKey().toString()));
                                logEventEntity.setSearchMap(super.searchMap(o));
                                if (logEventEntity != null) {
                                    list.add(logEventEntity);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("BatchAddEventOperator  batch update biz log error for : {} ", e.getMessage(), e);
        }
        return list;
    }

    private LogEventEntity compareFields(Object oldValue, Object newValue) {
        LogEventEntity logEventEntity = new LogEventEntity();

        List<LogEventFieldEntity> list = new ArrayList<>();
        if (oldValue instanceof BizLogStr) {
            oldValue = ((BizLogStr) oldValue).convertDoToVo(newValue.getClass());
            LogEventFieldEntity prefixBizLogStr = prefixBizLogStr(oldValue);
            list.add(prefixBizLogStr);
        }

        if (CollectionUtils.isEmpty(list)) {
            LogEventFieldEntity prefixBizLogStr = prefixBizLogStr(newValue);
            list.add(prefixBizLogStr);
        }
        // 获取变更结果
        List<LogEventFieldEntity> fieldEntities =
                BizLogStrComparator.compareFields(oldValue, newValue, new FieldDTO());
        list.addAll(fieldEntities);

        if (!CollectionUtils.isEmpty(list)) {
            logEventEntity.setList(list);
            return logEventEntity;
        }
        return null;
    }
}
