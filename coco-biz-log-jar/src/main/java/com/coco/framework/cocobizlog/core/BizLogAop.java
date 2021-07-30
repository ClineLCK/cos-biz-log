package com.coco.framework.cocobizlog.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.ttl.TtlCallable;
import com.coco.framework.cocobizlog.bean.CurrentLoginUserInfo;
import com.coco.framework.cocobizlog.bean.LogEventFieldEntity;
import com.coco.framework.cocobizlog.common.CosBizLogConstant;
import com.coco.framework.cocobizlog.core.annotation.BizLog;
import com.coco.framework.cocobizlog.core.enums.BaseLogEventEnum;
import com.coco.framework.cocobizlog.core.enums.BaseLogModuleEnum;
import com.coco.framework.cocobizlog.core.enums.LogEventEnum;
import com.coco.framework.cocobizlog.core.enums.LogModuleEnum;
import com.coco.framework.cocobizlog.core.event.EventOperator;
import com.coco.framework.cocobizlog.mesh.api.OkHttpRemoteApi;
import com.coco.framework.cocobizlog.service.BizLogBaseCommonService;
import com.coco.framework.cocobizlog.util.CosBizLogSpringContext;
import com.coco.framework.cocobizlog.util.IpUtils;
import com.coco.framework.cocobizlog.bean.LogEntity;
import com.coco.framework.cocobizlog.bean.LogEventEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import com.coco.terminal.cocobizlog.bean.HttpRestResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 业务日志Aop // todo 业务日志 分离到单独文件
 *
 * @author ckli01
 * @date 2018/8/31
 */
@Aspect
@Slf4j
public class BizLogAop {

    /**
     * 当前注解类信息
     */
    public static ThreadLocal<Class<?>> classThreadLocal =
            new ThreadLocal<>();
    /**
     * 当前登录人信息
     */
    private static ThreadLocal<CurrentLoginUserInfo> userInfoThreadLocal =
            new ThreadLocal<>();

    public static ThreadLocal<Object> prefixEntity = new ThreadLocal<>();


    @Autowired
    @Qualifier(value = "coco-biz-log-thread")
    private Executor executor;
    @Autowired
    private Environment environment;

    /**
     * 切点
     */
    @Pointcut("@annotation(com.coco.framework.cocobizlog.core.annotation.BizLog)")
    public void annotationAspect() {
    }

    @Around(value = "annotationAspect()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Class<?> clazz = pjp.getTarget().getClass();
        // 当前注解类信息
        classThreadLocal.set(clazz);
        // 获取当前登录人信息
        userInfoThreadLocal.set(currentUserInfo());
        // 日志体生成
        ThreadLogEntity threadLogEntity = createThreadLogEntity(pjp);
        // 记录日志
        Object o;
        try {
            o = pjp.proceed();
            // 日志体发送
            producerLogEntity(threadLogEntity, o);
        } finally {
            // 清除threadLocal 防止内存泄漏
            userInfoThreadLocal.remove();
            classThreadLocal.remove();
            prefixEntity.remove();
        }
        return o;
    }

    /**
     * 日志体发送
     *
     * @param threadLogEntity
     * @param o
     */
    private void producerLogEntity(ThreadLogEntity threadLogEntity, Object o) {
        executor.execute(
                () -> {
                    try {
                        //                ThreadLogEntity threadLogEntity = future.get();
                        // 方法返回信息封装
                        if (threadLogEntity.isFlag()) {
                            userDefinedLogEntity(o, threadLogEntity.getLogEntity());
                        }
                    } catch (Exception e) {
                        log.warn("BizAop last logEntity packaging catchException : {}", e.getMessage(), e);
                    }
                });
    }

    /**
     * 创建日志体
     *
     * @param pjp
     * @return
     */
    private ThreadLogEntity createThreadLogEntity(ProceedingJoinPoint pjp) {
        ThreadLogEntity threadLogEntity = new ThreadLogEntity();
        try {

            threadLogEntity.setLogEntity(logEntity(pjp));

            threadLogEntity.setFlag(true);
        } catch (Exception e) {
            log.warn("BizLogAop createThreadLogEntity error : {}", e.getMessage());
        }
        return threadLogEntity;
    }

    /**
     * 异步生成日志体
     *
     * @param pjp
     * @return
     */
    private Future<ThreadLogEntity> asyncFuture(ProceedingJoinPoint pjp) {
        Future<ThreadLogEntity> future =
                ((ThreadPoolTaskExecutor) executor)
                        .submit(
                                TtlCallable.get(
                                        () -> {
                                            return createThreadLogEntity(pjp);
                                        }));
        return future;
    }

    /**
     * 在类上获取日志模块
     *
     * @return
     */
    private LogModuleEnum getClassLogModule() {
        Class<?> clazz = classThreadLocal.get();
        BizLog bizLog = clazz.getAnnotation(BizLog.class);
        if (null != bizLog) {
            return bizLog.logModule();
        }
        return null;
    }

    /**
     * 在方法上获取日志注解信息
     *
     * @param pjp
     * @return
     */
    private BizLog getMethodBizLog(ProceedingJoinPoint pjp) throws Exception {
        Class<?> clazz = classThreadLocal.get();
        Class<?>[] par = ((MethodSignature) pjp.getSignature()).getParameterTypes();
        String methodName = pjp.getSignature().getName();
        Method method = clazz.getMethod(methodName, par);
        return method.getAnnotation(BizLog.class);
    }

    /**
     * 生成日志信息实体
     *
     * @param pjp
     * @return
     * @throws Exception
     */
    private LogEntity logEntity(ProceedingJoinPoint pjp) throws Exception {
        BizLog bizLog = getMethodBizLog(pjp);
        if (null == bizLog) {
            throw new Exception("BizAop cant find BizLog annotation");
        }
        LogEntity logEntity = new LogEntity();
        BaseLogModuleEnum baseLogModuleEnum = getBaseLogModuleEnum(bizLog);
        BaseLogEventEnum baseLogEventEnum = getBaseLogEventEnum(bizLog);
        boolean touch = bizLog.touch();

        logEntity.setModule(baseLogModuleEnum.getType());
        logEntity.setEvent(baseLogEventEnum.getType());

        logEntity.setServiceName(
                environment.containsProperty("spring.application.name")
                        ? environment.getProperty("spring.application.name")
                        : "UNKNOWN");
        logEntity.setMachineIp(IpUtils.getLocalIp());

        // 当前登陆人信息封装
        CurrentLoginUserInfo currentLoginUserInfo = userInfoThreadLocal.get();

        String personId = currentLoginUserInfo.getId() != null ? currentLoginUserInfo.getId() : "-1";
        String personName =
                StringUtils.isEmpty(currentLoginUserInfo.getName())
                        ? "SYSTEM"
                        : currentLoginUserInfo.getName();

        // 获取操作人Ip地址
        logEntity.setOperIp(currentLoginUserInfo.getIp());
        logEntity.setOperId(personId);

        StringBuilder sb = new StringBuilder();
        sb.append(baseLogModuleEnum.getDesc())
                .append(": ")
                .append(personName)
                .append(" ")
                .append(baseLogEventEnum.getDesc());

        // 统一 通用 前缀信息
        logEntity.setCommonPrefix(sb.toString());

        if (touch) {
            Object[] args = pjp.getArgs();
            if (!(null == args || args.length == 0)) {
                EventOperator eventOperator =
                        CosBizLogSpringContext.getBean(baseLogEventEnum.getOperatorClazz());

                // 对不同 操作 事件 的日志 进行 记录 处理
                // todo 是否可以 线程 异步 处理？
                List<LogEventEntity> list = eventOperator.operate(pjp.getArgs());
                logEntity.setLogEventEntities(list);
            }
        }

        return logEntity;
    }

    /**
     * 获取 日志 模块
     *
     * @param bizLog
     * @return
     */
    private BaseLogModuleEnum getBaseLogModuleEnum(BizLog bizLog) {
        try {
            String moduleEnumName = bizLog.moduleEnumName();
            LogModuleEnum logModuleEnum = bizLog.logModule();

            // 方法上没有module 类型 取 当前类上注解
            if (StringUtils.isEmpty(moduleEnumName)) {

                if (logModuleEnum.equals(LogModuleEnum.DEFAULT)) {
                    Class<?> clazz = classThreadLocal.get();
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

    /**
     * 获取 日志 事件
     *
     * @param bizLog
     * @return
     */
    private BaseLogEventEnum getBaseLogEventEnum(BizLog bizLog) {
        try {
            String eventEnumName = bizLog.eventEnumName();
            LogEventEnum logEventEnum = bizLog.logEvent();
            // 方法上没有module 类型 取 当前类上注解
            if (StringUtils.isEmpty(eventEnumName)) {
                return logEventEnum;
            } else {
                BizLogBaseCommonService commonService =
                        CosBizLogSpringContext.getBean(BizLogBaseCommonService.class);
                return commonService.logEventEnum(eventEnumName);
            }
        } catch (Exception e) {
            log.warn("BizLogAop getBaseLogEventEnum error : {}", e.getMessage());
            return LogEventEnum.DEFAULT;
        }
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    private CurrentLoginUserInfo currentUserInfo() {
        try {
            BizLogBaseCommonService baseService =
                    CosBizLogSpringContext.getBean(BizLogBaseCommonService.class);
            if (null != baseService) {
                CurrentLoginUserInfo currentLoginUserInfo = baseService.getCurrentLoginUserInfo();
                if (null == currentLoginUserInfo) {
                    currentLoginUserInfo = new CurrentLoginUserInfo();
                }
                log.info(
                        "BizLogAop currentUserInfo message :  {} ",
                        JSONObject.toJSONString(currentLoginUserInfo));
                return currentLoginUserInfo;
            }
        } catch (Exception e) {
            log.warn("BizLogAop currentUserInfo error for  :  {} ", e.getMessage());
        }
        return new CurrentLoginUserInfo();
    }

    /**
     * 根据不同返回类型，做不同日志记录
     *
     * @param o
     * @param logEntity
     */
    private void userDefinedLogEntity(Object o, LogEntity logEntity) {
        //        LogEventFieldEntity fieldEntity = AbstractEventOperator.suffixBizLogStr(o);

        formateLogEntity(logEntity);
        logEntity.setDate(Calendar.getInstance().getTime());
        // 重试次数三次
        HttpRestResult<Boolean> httpRestResult =
                CosBizLogSpringContext.getBean(OkHttpRemoteApi.class).add(logEntity);

        if (!httpRestResult.isSuccess()) {
            log.warn("send bizLog to cocoBizLog failed  message : {}", httpRestResult.getMessage());
        } else {
            log.info("send bizLog to cocoBizLog success");
        }
    }

    /**
     * 组装 日志 消息 结构 为 日志处理 中心 可以识别的结构
     *
     * @param logEntity
     */
    private void formateLogEntity(LogEntity logEntity) {
        if (!CollectionUtils.isEmpty(logEntity.getLogEventEntities())) {

            List<String> strList = new ArrayList<>();
            List<JSONObject> jsonObjects = new ArrayList<>();

            for (LogEventEntity logEventEntity : logEntity.getLogEventEntities()) {

                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(logEntity.getCommonPrefix()).append(CosBizLogConstant.MARK_NEW_LINE);

                JSONObject jsonObject = new JSONObject();

                for (LogEventFieldEntity logEventFieldEntity : logEventEntity.getList()) {
                    stringBuilder.append(logEventFieldEntity.formaterFieldStrZh());
                    // 转换json 对象
                    childJson(
                            logEventFieldEntity.getFiledName(), jsonObject, logEventFieldEntity.getResult());
                }

                // 自定义查询参数
                if (!CollectionUtils.isEmpty(logEventEntity.getSearchMap())) {
                    logEventEntity.getSearchMap().forEach((k, v) -> {
                        jsonObject.put(CosBizLogConstant.SEARCH_FIELD_PREFIX + k, v);
                    });
                }

                strList.add(stringBuilder.toString());
                jsonObjects.add(jsonObject);
            }
            logEntity.setCommonPrefix(null);
            logEntity.setLogEventEntities(null);
            logEntity.setEntitys(strList);
            logEntity.setEntityJsons(jsonObjects);
        }
    }

    /**
     * 转换 json 对象
     *
     * @param key
     * @param jsonObject
     * @param value
     */
    private void childJson(String key, JSONObject jsonObject, String value) {
        int first = key.indexOf(".");
        if (first < 0) {
            // 设置 单个属性, 如果 属性值已经存在，则转换为LIST
            if (jsonObject.containsKey(key)) {
                Object str = jsonObject.get(key);
                if (str instanceof List) {
                    ((List) str).add(value);
                } else {
                    List<String> list = new ArrayList<>();
                    list.add((String) str);
                    list.add(value);
                    jsonObject.put(key, list);
                }
            } else {
                jsonObject.put(key, value);
            }
            return;
        }
        String prefixKey = key.substring(0, first);
        String suffixKey = key.substring(first + 1);

        JSONObject child;
        if (jsonObject.containsKey(prefixKey)) {
            child = (JSONObject) jsonObject.get(prefixKey);
        } else {
            child = new JSONObject();
            jsonObject.put(prefixKey, child);
        }
        childJson(suffixKey, child, value);
    }

    /**
     * 线程处理Aop 日志处理
     */
    private class ThreadLogEntity {

        private boolean flag;
        private LogEntity logEntity;

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public LogEntity getLogEntity() {
            return logEntity;
        }

        public void setLogEntity(LogEntity logEntity) {
            this.logEntity = logEntity;
        }
    }
}
