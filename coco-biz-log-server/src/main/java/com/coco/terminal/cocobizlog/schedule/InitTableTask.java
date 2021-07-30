package com.coco.terminal.cocobizlog.schedule;

import com.coco.terminal.cocobizlog.lock.impl.CommonRedisDistributedLock;
import com.coco.terminal.cocobizlog.service.biz.LogEntityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * 初始化 表
 *
 * @author ckli01
 * @date 2019/11/25
 */
@Component
@Slf4j
public class InitTableTask {


    @Autowired
    private LogEntityService logEntityService;

    @Autowired
    private CommonRedisDistributedLock commonRedisDistributedLock;

    private static final String COCO_BIZ_LOG_DISTRIBUTE_LOCK = "coco_biz_log_distribute_lock";


    /**
     * 凌晨 4 点 33 分 33 秒触发
     *
     * @return
     */
    @Scheduled(cron = "33 33 04 * * *")
    @PostConstruct
    public void initTable() {
        try {
            LocalDate now = LocalDate.now();
            int year = now.getYear();
            int month = now.getMonthValue();
            if (commonRedisDistributedLock.lock(COCO_BIZ_LOG_DISTRIBUTE_LOCK)) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                int nextMonth = calendar.get(Calendar.MONTH) + 1;
                int nextYear = calendar.get(Calendar.YEAR);
                if (nextMonth != month || !logEntityService.existTable(year, nextMonth)) {
                    logEntityService.initTable(nextYear, nextMonth);
                    log.info("InitTableTask initTable year : {} month: {} ", now.getYear(), now.getMonthValue());
                } else {
                    log.info("InitTableTask initTable year : {} month: {} exist , do ignore ... ", now.getYear(), now.getMonthValue());
                }
                commonRedisDistributedLock.releaseLock(COCO_BIZ_LOG_DISTRIBUTE_LOCK);
            }
        } catch (Exception e) {
            log.error("initTable error for {}", e.getMessage(), e);
        }

    }


}

    
    
  