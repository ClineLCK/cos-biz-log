package com.coco.framework.cocobizlog.core.event.impl;

import com.coco.framework.cocobizlog.bean.FieldDTO;
import com.coco.framework.cocobizlog.bean.LogEventEntity;
import com.coco.framework.cocobizlog.bean.LogEventFieldEntity;
import com.coco.framework.cocobizlog.core.BizLogAop;
import com.coco.framework.cocobizlog.core.BizLogStr;
import com.coco.framework.cocobizlog.core.event.AbstractEventOperator;
import com.coco.framework.cocobizlog.core.event.EventOperator;
import com.coco.framework.cocobizlog.service.BizLogBaseBizService;
import com.coco.framework.cocobizlog.util.BizLogStrComparator;
import com.coco.framework.cocobizlog.util.CosBizLogSpringContext;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 更新 事件 处理
 *
 * @author ckli01
 * @date 2019-05-20
 */
@Slf4j
@Component
public class UpdateEventOperator extends AbstractEventOperator implements EventOperator {


    /**
     * 获取 更新 比较结果
     *
     * @param args
     * @return
     */
    @Override
    public List<LogEventEntity> operate(Object[] args) {
        List<LogEventEntity> list = super.operate(args);

        if (args[0] != null) {
            Object o = args[0];
            Class<?> clazz = BizLogAop.classThreadLocal.get();
            try {
                BizLogBaseBizService baseService =
                        (BizLogBaseBizService) CosBizLogSpringContext.getBean(clazz);
                if (null != baseService) {
                    // 获取修改之前值
                    Object oldObj = baseService.getPrefixEntityById(((BizLogStr) o).cocoKey());
                    BizLogAop.prefixEntity.set(oldObj);
                    if (oldObj instanceof BizLogStr) {
                        oldObj = ((BizLogStr) oldObj).convertDoToVo(o.getClass());
                    }

                    // 获取变更结果
                    List<LogEventFieldEntity> fieldEntities =
                            BizLogStrComparator.compareFields(oldObj, o, new FieldDTO());

                    if (!CollectionUtils.isEmpty(fieldEntities)) {
                        LogEventEntity logEventEntity;

                        if (CollectionUtils.isEmpty(list)) {
                            logEventEntity = new LogEventEntity();
                            list.add(logEventEntity);
                        } else {
                            logEventEntity = list.get(0);
                        }
                        logEventEntity.getList().addAll(fieldEntities);

                        if (logEventEntity.getSearchMap() == null) {
                            logEventEntity.setSearchMap(super.searchMap(o));
                        }
                    }
                }
            } catch (Exception e) {
                log.warn(
                        "UpdateEventOperator prefixBizLogStr error for class: {} message: {}",
                        BizLogAop.classThreadLocal.get().getName(),
                        e.getMessage());
            }
        }
        return list;
    }
}
