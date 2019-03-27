package com.nfsq.framework.cosbizlog.aop;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlCallable;
import com.nfsq.framework.cosbizlog.annotation.BizLog;
import com.nfsq.framework.cosbizlog.bean.BizLogStr;
import com.nfsq.framework.cosbizlog.bean.CurrentLoginUserInfo;
import com.nfsq.framework.cosbizlog.bean.HttpRestResult;
import com.nfsq.framework.cosbizlog.bean.LogEntity;
import com.nfsq.framework.cosbizlog.enums.BaseLogEventEnum;
import com.nfsq.framework.cosbizlog.enums.BaseLogModuleEnum;
import com.nfsq.framework.cosbizlog.enums.LogEventEnum;
import com.nfsq.framework.cosbizlog.enums.LogModuleEnum;
import com.nfsq.framework.cosbizlog.mesh.CocoBizLogApi;
import com.nfsq.framework.cosbizlog.service.BizLogBaseBizService;
import com.nfsq.framework.cosbizlog.service.BizLogBaseCommonService;
import com.nfsq.framework.cosbizlog.util.CosBizLogConstant;
import com.nfsq.framework.cosbizlog.util.CosBizLogSpringContext;
import com.nfsq.framework.cosbizlog.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;


/**
 * 业务日志Aop
 * // todo 业务日志 分离到单独文件
 *
 * @author ckli01
 * @date 2018/8/31
 */
@Aspect
@Slf4j
public class BizLogAop {

    @Autowired
    private CocoBizLogApi cocoBizLogApi;

    @Autowired
    private Executor executor;

    @Autowired
    private Environment environment;


    /**
     * 当前注解类信息
     */
    private static TransmittableThreadLocal<Class<?>> classThreadLocal = new TransmittableThreadLocal<>();
    /**
     * 当前登录人信息
     */
    private static TransmittableThreadLocal<CurrentLoginUserInfo> userInfoThreadLocal = new TransmittableThreadLocal<>();


    public static TransmittableThreadLocal<JSONObject> entityJsonThreadLocal = new TransmittableThreadLocal<>();


    /**
     * 切点
     */
    @Pointcut("@annotation(com.nfsq.framework.cosbizlog.annotation.BizLog)")
    public void annotationAspect() {
    }

//    @Before(value = "annotationAspect()")
//    public void before(JoinPoint joinPoint) {
//    }

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
        Object o = pjp.proceed();
        // 日志体发送
        producerLogEntity(threadLogEntity, o);
        // 清除threadLocal 防止内存泄漏
        userInfoThreadLocal.remove();
        classThreadLocal.remove();
        return o;
    }

    /**
     * 日志体发送
     *
     * @param threadLogEntity
     * @param o
     */
    private void producerLogEntity(ThreadLogEntity threadLogEntity, Object o) {
        threadLogEntity.getLogEntity().setEntityJson(entityJsonThreadLocal.get());
        entityJsonThreadLocal.remove();
        executor.execute(() -> {
            try {
//                ThreadLogEntity threadLogEntity = future.get();
                // 方法返回信息封装
                if (threadLogEntity.isFlag()) {
                    userDefinedLogEntity(o, threadLogEntity.getLogEntity());
                }
            } catch (Exception e) {
                log.warn("BizAop last logEntity packaging catchException : {}", e.getMessage(), e);
            } finally {
                entityJsonThreadLocal.remove();
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
        Future<ThreadLogEntity> future = ((ThreadPoolTaskExecutor) executor).submit(TtlCallable.get(() -> {
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


        logEntity.setServiceName(environment.containsProperty("spring.application.name") ? environment.getProperty("spring.application.name")
                : "UNKNOWN");
        logEntity.setMachineIp(IpUtils.getLocalIp());


        // 当前登陆人信息封装
        CurrentLoginUserInfo currentLoginUserInfo = userInfoThreadLocal.get();

        Long personId = currentLoginUserInfo.getId() != null ? currentLoginUserInfo.getId() : -1L;
        String personName = StringUtils.isEmpty(currentLoginUserInfo.getName()) ? "SYSTEM" : currentLoginUserInfo.getName();
        // 获取操作人Ip地址
        logEntity.setOperIp(currentLoginUserInfo.getIp());
        logEntity.setOperId(personId);

        StringBuilder sb = new StringBuilder();
        sb.append(baseLogModuleEnum.getDesc())
                .append(": ")
                .append(personName)
                .append(" ")
                .append(baseLogEventEnum.getDesc());

        // 前缀信息
        logEntity.setEntity(sb.toString());

        entityJsonThreadLocal.set(new JSONObject());

        // 操作参数
        setArgsIntoLogEntity(baseLogEventEnum, logEntity, pjp.getArgs(), touch);

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
                            BizLogBaseCommonService commonService = CosBizLogSpringContext.getBean(BizLogBaseCommonService.class);
                            return commonService.logModuleEnum(faBizLog.moduleEnumName());
                        } else {
                            return faBizLog.logModule();
                        }
                    }

                }
                return bizLog.logModule();
            } else {
                BizLogBaseCommonService commonService = CosBizLogSpringContext.getBean(BizLogBaseCommonService.class);
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
                BizLogBaseCommonService commonService = CosBizLogSpringContext.getBean(BizLogBaseCommonService.class);
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
            BizLogBaseCommonService baseService = CosBizLogSpringContext.getBean(BizLogBaseCommonService.class);
            if (null != baseService) {
                CurrentLoginUserInfo currentLoginUserInfo = baseService.getCurrentLoginUserInfo();
                if (null == currentLoginUserInfo) {
                    currentLoginUserInfo = new CurrentLoginUserInfo();
                }
                log.info("BizLogAop currentUserInfo message :  {} ", JSONObject.toJSONString(currentLoginUserInfo));
                return currentLoginUserInfo;
            }
        } catch (Exception e) {
            log.warn("BizLogAop currentUserInfo error for  :  {} ", e.getMessage());
        }
        return new CurrentLoginUserInfo();
    }

    /**
     * 参数获取并写入日志信息
     * 目前只考虑第一个参数作为校验值
     *
     * @param baseLogEventEnum
     * @param logEntity
     * @param args
     */
    private void setArgsIntoLogEntity(BaseLogEventEnum baseLogEventEnum, LogEntity logEntity, Object[] args, boolean touch) {
        if (null == args || args.length == 0) {
            return;
        }
        String str = "";
        // touch -> true  触发时间对应各处理
        if (touch && baseLogEventEnum instanceof LogEventEnum) {
            LogEventEnum logEventEnum = (LogEventEnum) baseLogEventEnum;
            switch (logEventEnum) {
                case ADD:
                    break;
                case DELETE:
                case UPDATE:
                    str = prefixBizLogStr(args[0]);
                    break;
                case SUBMIT:
                    break;
                default:
                    break;
            }
        }
        formateLogEntityStr(logEntity, str);
    }


    /**
     * 根据不同返回类型，做不同日志记录
     *
     * @param o
     * @param logEntity
     */
    private void userDefinedLogEntity(Object o, LogEntity logEntity) {
        String suffixBizLogStr = suffixBizLogStr(o);
        logEntity.getEntityJson().put("bizlog_suffix", suffixBizLogStr);
        formateLogEntityStr(logEntity, suffixBizLogStr);
        logEntity.setDate(Calendar.getInstance().getTime());
        // 重试次数三次
        for (int i = 0; i < CosBizLogConstant.MAX_RETRY_TIMES; i++) {

            HttpRestResult<Boolean> httpRestResult = cocoBizLogApi.add(logEntity);

            if (!httpRestResult.isSuccess()) {
                log.warn("send bizLog to cocoBizLog failed retryTimes: {} code: {}, message : {}", i,
                        httpRestResult.getCode(), httpRestResult.getMessage());
            } else {
                log.info("send bizLog to cocoBizLog success");
                break;
            }
        }
    }

    /**
     * 获取实体自定义日志信息，后缀
     * // todo 考虑不同返回值
     *
     * @param o
     * @return
     */
    private String suffixBizLogStr(Object o) {
        String str;
        if (isSubClassOfBizlogStr(o)) {
            str = ((BizLogStr) o).suffixBizLogStr();
        } else {
            str = getUsualLogStr(o);
        }
        return str;
    }

    /**
     * 获取实体自定义日志信息，前缀
     * // todo 考虑不同传值
     *
     * @param o
     * @return
     */
    private String prefixBizLogStr(Object o) {
        String str;
        if (isSubClassOfBizlogStr(o)) {
            str = ((BizLogStr) o).prefixBizLogStr();
            entityJsonThreadLocal.get().put("bizlog_prefix", str);
            Class<?> clazz = classThreadLocal.get();
            try {
                BizLogBaseBizService baseService = (BizLogBaseBizService) CosBizLogSpringContext.getBean(clazz);
                if (null == baseService) {
                    return str;
                }
                // 获取修改之前do
                Object oldObj = baseService.getById(((BizLogStr) o).getId());
                BizLogStr bizLogStr = (BizLogStr) oldObj;
                // 获取变更结果
                String compareFields = bizLogStr.compareFields(o);

                if (!StringUtils.isEmpty(compareFields)) {
                    str += CosBizLogConstant.MARK_NEW_LINE + compareFields;
                }
            } catch (Exception e) {
                log.warn("prefixBizLogStr error for class: {} message: {}", classThreadLocal.get().getName(),
                        e.getMessage(), e);
            }
        } else {
            str = getUsualLogStr(o);
        }
        return str;
    }

    private String getUsualLogStr(Object o) {
        String str = "";
        if (null == o) {
            return str;
        } else if (o instanceof Collection) {
            str = JSONObject.toJSONString(o);
        } else if (o instanceof Map) {
            str = JSONObject.toJSONString(o);
        } else {
            str = JSONObject.toJSONString(o);
        }
        return str;
    }


    /**
     * 是否是BizLogStr 的子类
     *
     * @param o
     * @return
     */
    private boolean isSubClassOfBizlogStr(Object o) {
        return o instanceof BizLogStr;
    }


    /**
     * 对日志信息给定格式封装
     *
     * @param logEntity
     * @param str
     */
    private void formateLogEntityStr(LogEntity logEntity, String str) {
        if (!StringUtils.isEmpty(str)) {
            logEntity.setEntity(logEntity.getEntity() + CosBizLogConstant.MARK_NEW_LINE + str);
        }
    }

    /**
     * 线程处理Aop 日志处理
     */
    private class ThreadLogEntity extends LogEntity {
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
