package com.coco.framework.cocobizlog.core;

import com.coco.framework.cocobizlog.core.annotation.BizLog;
import com.coco.framework.cocobizlog.core.enums.BaseLogModuleEnum;
import com.coco.framework.cocobizlog.core.enums.LogModuleEnum;
import com.coco.framework.cocobizlog.mesh.api.OkHttpRemoteApi;
import com.coco.framework.cocobizlog.service.AbstractBizLogSearchService;
import com.coco.framework.cocobizlog.service.BizLogBaseBizService;
import com.coco.framework.cocobizlog.service.BizLogBaseCommonService;
import com.coco.framework.cocobizlog.util.CosBizLogSpringContext;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.coco.terminal.cocobizlog.bean.HttpRestResult;
import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 初始化
 *
 * @author ckli01
 * @date 2019/11/9
 */
@Slf4j
public class BizLogIniter {


  public BizLogIniter() {
    init();
  }

  private void init() {
    log.info("BizLogIniter init start...");
    Environment environment = CosBizLogSpringContext.getBean(Environment.class);
    String appName = environment.getProperty("spring.application.name");

    // 若应用名找不到 则 终止 初始化
    if (StringUtils.isEmpty(appName)) {
      log.error("BizLogIniter init stoped of the spring.application.name is empty");
      return;
    }

    Map<Integer, Map> moduleFieldsMap = new HashMap<>();

    // 获取 所有记录了日志的服务类
    Map<String, BizLogBaseBizService> map = CosBizLogSpringContext.getApplicationContext()
        .getBeansOfType(BizLogBaseBizService.class);

    if (!CollectionUtils.isEmpty(map)) {

      map.forEach((k, v) -> {
        Method[] methods = v.getClass().getDeclaredMethods();

        for (Method method : methods) {
          // 获取 所有配置了 日志记录的方法
          BizLog bizLog = AnnotationUtils.findAnnotation(method, BizLog.class);

          if (bizLog != null) {
            Parameter[] parameters = method.getParameters();
            BaseLogModuleEnum moduleEnum = getBaseLogModuleEnum(bizLog, v.getClass());

            // 判断当前 moduleFieldsMap  是否已经存在  这个module
            if (moduleFieldsMap.containsKey(moduleEnum.getType())) {
              continue;
            }
            log.info("BizLogIniter init will scan module for {}-{}", moduleEnum.getType(),
                moduleEnum.getDesc());
            if (parameters != null) {
              Parameter parameter = parameters[0];
              Class<?> classType = parameter.getType();
              //分别判断参数为数组或集合并获取泛型和数组类型
              if (BizLogStr.class.isAssignableFrom(classType)) {
                //
                Map<String, String> fieldsMap = AbstractBizLogSearchService
                    .staticFieldsMap((Class<? extends BizLogStr>) classType, true);
                moduleFieldsMap.put(moduleEnum.getType(), fieldsMap);
              } else if (Collection.class.isAssignableFrom(classType)) {
                // todo  获取 Collection 范型类型
              }
            }
          }

        }
      });
    }

    if (!CollectionUtils.isEmpty(moduleFieldsMap)) {
      log.info("BizLogIniter init will send LogEntityDO to init index if not exist...");
      List<LogEntityDO> entityDOS = new ArrayList<>();
      moduleFieldsMap.forEach((k, v) -> {
        LogEntityDO logEntityDO = new LogEntityDO();

        logEntityDO.setModule(k);
        logEntityDO.setServiceName(appName);
        logEntityDO.setEntityJson(v);
        entityDOS.add(logEntityDO);
      });
      //  将数据 发送到 日志中心

      // 重试次数三次
      HttpRestResult<Boolean> httpRestResult =
          CosBizLogSpringContext.getBean(OkHttpRemoteApi.class).initIndex(entityDOS);

      if (!httpRestResult.isSuccess()) {
        log.error("BizLogIniter init from log center is failed , message : {}",
            httpRestResult.getMessage());
      } else {
        log.info("BizLogIniter init from log center is success...");
      }
    }
    log.info("BizLogIniter init end...");
  }


  /**
   * 获取 日志 模块
   *
   * @param bizLog
   * @return
   */
  private BaseLogModuleEnum getBaseLogModuleEnum(BizLog bizLog, Class<?> clazz) {
    try {
      String moduleEnumName = bizLog.moduleEnumName();
      LogModuleEnum logModuleEnum = bizLog.logModule();

      // 方法上没有module 类型 取 当前类上注解
      if (StringUtils.isEmpty(moduleEnumName)) {

        if (logModuleEnum.equals(LogModuleEnum.DEFAULT)) {
          BizLog faBizLog = clazz.getAnnotation(BizLog.class);
          if (null != faBizLog) {
            // 当前类上注解 也没有自定义 注解 内容，则返回父类logModule
            if (!StringUtils.isEmpty(faBizLog.eventEnumName())) {
              BizLogBaseCommonService commonService =
                  CosBizLogSpringContext.getBean(BizLogBaseCommonService.class);
              return commonService.logModuleEnum(faBizLog.moduleEnumName());
            } else {
              return faBizLog.logModule();
            }
          }
        }
        return bizLog.logModule();
      } else {
        BizLogBaseCommonService commonService =
            CosBizLogSpringContext.getBean(BizLogBaseCommonService.class);
        return commonService.logModuleEnum(moduleEnumName);
      }
    } catch (Exception e) {
      log.warn("BizLogAop getBaseLogModuleEnum error : {}", e.getMessage());
      return LogModuleEnum.DEFAULT;
    }
  }


}

    
    
  