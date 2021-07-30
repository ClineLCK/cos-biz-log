package com.coco.framework.cocobizlog.core.event.impl;

import com.coco.framework.cocobizlog.bean.LogEventEntity;
import com.coco.framework.cocobizlog.bean.LogEventFieldEntity;
import com.coco.framework.cocobizlog.core.BizLogStr;
import com.coco.framework.cocobizlog.core.enums.LogEventFieldTypeEnum;
import com.coco.framework.cocobizlog.core.event.AbstractEventOperator;
import com.coco.framework.cocobizlog.core.event.EventOperator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 删除操作 事件 处理
 *
 * @author ckli01
 * @date 2019-05-21
 */
@Component
public class DeleteEventOperator extends AbstractEventOperator implements EventOperator {

    @Override
    public List<LogEventEntity> operate(Object[] args) {
        List<LogEventEntity> list = new ArrayList<>();

        if (args[0] != null) {
            LogEventEntity logEventEntity = new LogEventEntity();

            List<LogEventFieldEntity> fieldEntities = new ArrayList<>();
            // 字段变更 记录
            LogEventFieldEntity logEventFieldEntity = new LogEventFieldEntity();

            logEventFieldEntity.setFiledName("bizlog_prefix");
            logEventFieldEntity.setFiledNameZh("关键字");
            logEventFieldEntity.setLogEventFieldTypeEnum(LogEventFieldTypeEnum.BODY);
            if (args[0] instanceof BizLogStr) {
                Object o = args[0];
                Object cocoKey = ((BizLogStr) o).cocoKey();
                logEventFieldEntity.setResult(cocoKey != null ? cocoKey.toString() : "");
            } else {
                logEventFieldEntity.setResult(args[0] != null ? args[0].toString() : "");
            }

            fieldEntities.add(logEventFieldEntity);

            logEventEntity.setList(fieldEntities);

            logEventEntity.setSearchMap(super.searchMap(args[0]));
            list.add(logEventEntity);
        }
        return list;
    }
}
