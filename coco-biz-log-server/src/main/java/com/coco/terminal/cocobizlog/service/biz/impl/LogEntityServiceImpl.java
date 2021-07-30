//package com.coco.terminal.cocobizlog.service.biz.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import com.coco.terminal.cocobizlog.bean.LogEntityDO;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.Lists;
//import com.coco.terminal.cocobizlog.dal.domain.LogEntityDomain;
//import com.coco.terminal.cocobizlog.dal.mapper.LogEntityMapper;
//import com.coco.terminal.cocobizlog.entity.EsEntity;
//import com.coco.terminal.cocobizlog.entity.EsLogEntity;
//import com.coco.terminal.cocobizlog.service.biz.LogEntityService;
//import com.coco.terminal.cocobizlog.service.es.EsBaseService;
//import com.coco.terminal.cocobizlog.util.EsIndexUtil;
//import com.coco.terminal.cocobizlog.util.GzipUtil;
//import com.coco.terminal.cocobizlog.util.WrappedBeanCopier;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.CollectionUtils;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
///**
// * 日志服务实现类
// *
// * @author ckli01
// * @date 2018/8/31
// */
//@Service
//@Slf4j
//public class LogEntityServiceImpl extends BaseServiceImpl<LogEntityDO, LogEntityMapper> implements LogEntityService {
//
//
//    @Autowired
//    private EsBaseService esBaseService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void dealDataToDBAndEs(List<LogEntityDO> logEntities, List<EsEntity> esEntities) throws Exception {
//        // todo 根据 时间选择表
//        for (LogEntityDO logEntityDO : logEntities) {
//            Date date = logEntityDO.getDate();
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            LogEntityDomain logEntityDomain= WrappedBeanCopier.copyProperties(logEntityDO,LogEntityDomain.class);
//            logEntityDomain.setMonth(calendar.get(Calendar.MONTH) + 1);
//            logEntityDomain.setYear(calendar.get(Calendar.YEAR));
//
//            super.getBaseMapper().insertCurrent(logEntityDomain);
//        }
//
//        if (!esBaseService.bulkAdd(esEntities)) {
//            throw new Exception("LogEntityServiceImpl dealDataToDBAndEs es add msgs error");
//        }
//    }
//
//
//    @Override
//    public void initTable(Integer year, Integer month) {
//        super.getBaseMapper().initTable(year, month);
//    }
//
//    @Override
//    public boolean existTable(int year, int month) {
//        return super.getBaseMapper().existTable(year, month) > 0;
//    }
//
//
//    @Override
//    public boolean dealMsg(List<String> msgs) {
//        List<LogEntityDO> logEntities = Lists.newArrayList();
//        List<EsEntity> esEntities = Lists.newArrayList();
//
//        for (String str : msgs) {
//            try {
//                String ss = GzipUtil.uncompress(str);
//                LogEntityDTO logEntityDTO = objectMapper.readValue(ss == null ? str : ss, LogEntityDTO.class);
//
//                if (CollectionUtils.isEmpty(logEntityDTO.getEntityJsons())) {
//                    LogEntityDO logEntityDO = objectMapper.readValue(ss == null ? str : ss, LogEntityDO.class);
//                    addLogEntityDoToList(logEntities, esEntities, logEntityDO);
//                } else {
//                    for (int i = 0; i < logEntityDTO.getEntitys().size(); i++) {
//                        LogEntityDO logEntityDO = WrappedBeanCopier.copyProperties(logEntityDTO, LogEntityDO.class);
//                        logEntityDO.setEntityJson(logEntityDTO.getEntityJsons().get(i));
//                        logEntityDO.setEntity(logEntityDTO.getEntitys().get(i));
//                        addLogEntityDoToList(logEntities, esEntities, logEntityDO);
//                    }
//                }
//            } catch (Exception e) {
//                log.error("rocketMq consumer msgs package failed : {}", e.getMessage(), e);
//            }
//        }
//
//        try {
//            dealDataToDBAndEs(logEntities, esEntities);
//        } catch (Exception e) {
//            //RECONSUME_LATER 消费失败，需要稍后重新消费
//            log.error("rocketMq consumer msgs failed: {} \r\n" +
//                            "logEntities: {} \r\n" +
//                            "esEntities: {} \r\n", e.getMessage(),
//                    JSONObject.toJSONString(logEntities),
//                    JSONObject.toJSONString(esEntities),
//                    e);
//            // 全部消费成功，不影响 mq 运行，后期 通过日志 手机 进行补偿保证数据
//        }
//        return true;
//    }
//
//
//    private void addLogEntityDoToList(List<LogEntityDO> logEntities, List<EsEntity> esEntities, LogEntityDO logEntityDO) {
////        // 最长40960字符
////        if (logEntityDO.getEntity().length() > 40960) {
////            logEntityDO.setEntity(logEntityDO.getEntity().substring(0, 40960));
////        }
//        logEntities.add(logEntityDO);
//
//        // es 日志体封装
//        EsEntity esEntity = new EsEntity();
//
//
//        EsLogEntity esLogEntity = WrappedBeanCopier.copyProperties(logEntityDO, EsLogEntity.class);
//        try {
//            esEntity.setDoc(objectMapper.writeValueAsString(esLogEntity));
//        } catch (JsonProcessingException e) {
//            log.error(e.getMessage(), e);
//        }
//
//        esEntity.setIndex(EsIndexUtil.index(logEntityDO));
//        esEntity.setType(esEntity.getIndex());
//
//        esEntities.add(esEntity);
//    }
//
//}
