package com.coco.framework.cocobizlog.core.event.impl;

import com.coco.framework.cocobizlog.bean.FieldDTO;
import com.coco.framework.cocobizlog.bean.LogEventEntity;
import com.coco.framework.cocobizlog.bean.LogEventFieldEntity;
import com.coco.framework.cocobizlog.core.BizLogAop;
import com.coco.framework.cocobizlog.core.event.AbstractEventOperator;
import com.coco.framework.cocobizlog.core.event.EventOperator;
import com.coco.framework.cocobizlog.util.BizLogStrComparator;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 添加 事件 处理
 *
 * @author ckli01
 * @date 2019-05-20
 */
@Component
@Slf4j
public class AddEventOperator extends AbstractEventOperator implements EventOperator {

    @Override
    public List<LogEventEntity> operate(Object[] args) {
        List<LogEventEntity> list = super.operate(args);

        if (args[0] != null) {
            Object o = args[0];
            try {
                // 获取变更结果
                List<LogEventFieldEntity> fieldEntities =
                        BizLogStrComparator.compareFields(o.getClass().newInstance(), o, new FieldDTO());

                if (!CollectionUtils.isEmpty(fieldEntities)) {
                    LogEventEntity logEventEntity;

                    if (CollectionUtils.isEmpty(list)) {
                        logEventEntity = new LogEventEntity();
                        list.add(logEventEntity);
                    } else {
                        logEventEntity = list.get(0);
                    }
                    logEventEntity.getList().addAll(fieldEntities);

                    if(logEventEntity.getSearchMap()==null){
                        logEventEntity.setSearchMap(super.searchMap(o));
                    }
                }
            } catch (Exception e) {
                log.warn(
                        "AddEventOperator prefixBizLogStr error for class: {} message: {}",
                        BizLogAop.classThreadLocal.get().getName(),
                        e.getMessage());
            }

        }
        return list;
    }
}
