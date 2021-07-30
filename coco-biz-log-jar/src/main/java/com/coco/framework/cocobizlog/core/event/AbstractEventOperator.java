package com.coco.framework.cocobizlog.core.event;

import com.coco.framework.cocobizlog.core.enums.LogEventFieldTypeEnum;
import com.coco.framework.cocobizlog.bean.LogEventEntity;
import com.coco.framework.cocobizlog.bean.LogEventFieldEntity;
import com.coco.framework.cocobizlog.core.BizLogAop;
import com.coco.framework.cocobizlog.core.BizLogStr;
import com.coco.framework.cocobizlog.service.BizLogBaseBizService;
import com.coco.framework.cocobizlog.util.CosBizLogSpringContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 事件操作 抽象类
 *
 * @author ckli01
 * @date 2019-05-21
 */
@Slf4j
public abstract class AbstractEventOperator implements EventOperator {

    /**
     * 是否是BizLogStr 的子类
     *
     * @param o
     * @return
     */
    public static boolean isSubClassOfBizlogStr(Object o) {
        return o instanceof BizLogStr;
    }

    /**
     * 获取实体自定义日志信息，后缀
     *
     * @param o
     * @return
     */
    public static LogEventFieldEntity suffixBizLogStr(Object o) {
        LogEventFieldEntity logEventEntity = null;

        if (isSubClassOfBizlogStr(o)) {
            logEventEntity = new LogEventFieldEntity();

            String str = ((BizLogStr) o).suffixBizLogStr();
            logEventEntity.setLogEventFieldTypeEnum(LogEventFieldTypeEnum.SUFFIX);

            logEventEntity.setResult(str);
        }
        return logEventEntity;
    }

    /**
     * 默认 封装 自定义前缀 信息
     *
     * @param args
     * @return
     */
    @Override
    public List<LogEventEntity> operate(Object[] args) {
        return operate(args[0]);
    }

    @Override
    public List<LogEventEntity> operate(Object o) {
        List<LogEventEntity> list = new ArrayList<>();

        if (o != null) {
            LogEventFieldEntity entity = prefixBizLogStr(o);
            if (entity != null) {
                List<LogEventFieldEntity> logEventFieldEntities = new ArrayList<>();
                logEventFieldEntities.add(entity);

                LogEventEntity logEventEntity = new LogEventEntity();
                logEventEntity.setList(logEventFieldEntities);


                logEventEntity.setSearchMap(searchMap(o));

                list.add(logEventEntity);
            }
        }
        return list;
    }

    /**
     * 获取实体自定义日志信息，前缀
     *
     * @param o
     * @return
     */
    protected LogEventFieldEntity prefixBizLogStr(Object o) {
        LogEventFieldEntity logEventEntity = null;

        if (isSubClassOfBizlogStr(o)) {
            logEventEntity = new LogEventFieldEntity();

            String str = ((BizLogStr) o).prefixBizLogStr();
            logEventEntity.setLogEventFieldTypeEnum(LogEventFieldTypeEnum.PREFIX);

            logEventEntity.setResult(str);
        }
        return logEventEntity;
    }


  /**
   * 自定义业务查询参数
   * @param o
   * @return
   */
    protected Map<String, String> searchMap(Object o) {
        Class<?> clazz = BizLogAop.classThreadLocal.get();
        try {
            BizLogBaseBizService baseService =
                    (BizLogBaseBizService) CosBizLogSpringContext.getBean(clazz);
            return baseService.getSearchEntity(o);
        } catch (Exception e) {
            log.warn("AbstractEventOperator searchMap error for {}", e.getMessage());
        }
        return null;
    }


}
