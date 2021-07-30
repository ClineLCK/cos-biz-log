package com.coco.framework.cosbizlog.aop;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.coco.framework.cosbizlog.annotation.BizLog;
import com.coco.framework.cosbizlog.bean.*;
import com.coco.framework.cosbizlog.mesh.CocoBizLogApi;
import com.coco.framework.cosbizlog.service.BizLogBaseService;
import com.coco.framework.cosbizlog.util.CosBizLogConstant;
import com.coco.framework.cosbizlog.util.CosBizLogSpringContext;
import com.coco.framework.cosbizlog.util.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 业务日志Aop
 * // todo 业务日志 分离到单独文件
 *
 * @author clinechen
 * @date 2018/8/31
 */
@Aspect
@Slf4j
public class BizLogAop {

    @Autowired
    private CocoBizLogApi cocoBizLogApi;

    @Autowired
    private Executor executor;

    @Value("${spring.application.name}")
    private String serviceName;

    /**
     * 当前注解类信息
     */
    private static TransmittableThreadLocal<Class<?>> classThreadLocal = new TransmittableThreadLocal<>();
    /**
     * 当前登录人信息
     */
    private static TransmittableThreadLocal<CurrentLoginUserInfo> userInfoThreadLocal = new TransmittableThreadLocal<>();

    /**
     * 切点
     */
    @Pointcut("@annotation(com.coco.framework.cosbizlog.annotation.BizLog)")
    public void annotationAspect() {
    }

    @Around(value = "annotationAspect()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Class<?> clazz = pjp.getTarget().getClass();

        // 当前注解类信息
        classThreadLocal.set(clazz);
        // 获取当前登录人信息
        userInfoThreadLocal.set(currentUserInfo());


//        Future<ThreadLogEntity> future = ((ThreadPoolTaskExecutor) executor).submit(TtlCallable.get(() -> {
        ThreadLogEntity threadLogEntity = new ThreadLogEntity();
        try {
            threadLogEntity.setLogEntity(logEntity(pjp));
            threadLogEntity.setFlag(true);
        } catch (Exception e) {
            log.error("BizAop logEntity catchException : {}", e.getMessage(), e);
        }
//            return threadLogEntity;
//        }));

        // 记录日志，不得影响正常程序运行，包括耗时
        Object o = pjp.proceed();
        try {

//            ThreadLogEntity threadLogEntity = future.get();
            // 方法返回信息封装
            if (threadLogEntity.isFlag()) {
                userDefinedLogEntity(o, threadLogEntity.getLogEntity());
            }
        } catch (Exception e) {
            log.error("BizAop last logEntity packaging catchException : {}", e.getMessage(), e);
        } finally {
            userInfoThreadLocal.remove();
            classThreadLocal.remove();
        }
        return o;
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
        LogModuleEnum logModuleEnum = bizLog.logModule();
        LogEventEnum logEventEnum = bizLog.logEvent();

        if (LogModuleEnum.DEFAULT.equals(bizLog.logModule())) {
            LogModuleEnum logModule = getClassLogModule();
            logModuleEnum = logModule != null ? logModule : logModuleEnum;
        }

        logEntity.setServiceName(serviceName);
        logEntity.setMachineIp(IpUtils.getLocalIp());

        logEntity.setModule(logModuleEnum.getType());
        logEntity.setEvent(bizLog.logEvent().getType());

        CurrentLoginUserInfo currentLoginUserInfo = userInfoThreadLocal.get();

        Long personId = currentLoginUserInfo.getId() != null ? currentLoginUserInfo.getId() : 0L;
        String personName = StringUtils.isEmpty(currentLoginUserInfo.getName()) ? "SYSTEM" : currentLoginUserInfo.getName();

        logEntity.setOperId(personId);
        // 前缀信息
        logEntity.setEntity(logModuleEnum.getDesc() + ": " + personName + " " + logEventEnum.getDesc());
        // 操作参数
        setArgsIntoLogEntity(logEventEnum, logEntity, pjp.getArgs());

        // 获取操作人Ip地址
        logEntity.setOperIp(currentLoginUserInfo.getIp());

        return logEntity;
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    private CurrentLoginUserInfo currentUserInfo() {
        Class<?> clazz = classThreadLocal.get();
        BizLogBaseService baseService = (BizLogBaseService) CosBizLogSpringContext.getBean(clazz);
        CurrentLoginUserInfo currentLoginUserInfo = baseService.getCurrentLoginUserInfo();
        if (null == currentLoginUserInfo) {
            currentLoginUserInfo = new CurrentLoginUserInfo();
        }
        return currentLoginUserInfo;
    }

    /**
     * 参数获取并写入日志信息
     * 目前只考虑第一个参数作为校验值
     *
     * @param logEventEnum
     * @param logEntity
     * @param args
     */
    private void setArgsIntoLogEntity(LogEventEnum logEventEnum, LogEntity logEntity, Object[] args) {
        if (null == args || args.length == 0) {
            return;
        }
        String str = "";
        switch (logEventEnum) {
            case DELETE:
            case UPDATE:
                str = prefixBizLogStr(args[0]);
                break;
            default:
                break;
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
        try {
            formateLogEntityStr(logEntity, suffixBizLogStr(o));
            logEntity.setDate(Calendar.getInstance().getTime());


            executor.execute(() -> {
                // 重试次数三次
                for (int i = 0; i < CosBizLogConstant.MAX_RETRY_TIMES; i++) {

                    HttpRestResult<Boolean> httpRestResult = cocoBizLogApi.add(logEntity);

                    if (!httpRestResult.isSuccess()) {
                        log.error("send bizLog to cocoBizLog failed retryTimes: {} code: {}, message : {}", i,
                                httpRestResult.getCode(), httpRestResult.getMessage());
                    } else {
                        log.debug("send bizLog to cocoBizLog success");
                        break;
                    }
                }

            });


        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
            Class<?> clazz = classThreadLocal.get();
            BizLogBaseService baseService = (BizLogBaseService) CosBizLogSpringContext.getBean(clazz);
            try {
                if (null == baseService) {
                    return str;
                }
                // 获取修改之前do
                Object oldObj = baseService.getById(((BizLogStr) o).getId());
                BizLogStr bizLogStr = (BizLogStr) oldObj;
                // 将do 转化为 VO
                Object oldVo = bizLogStr.convertDoToVo(o.getClass());
                // 获取变更结果
                String compareFields = ((BizLogStr) oldVo).compareFields(o);

                if (!StringUtils.isEmpty(compareFields)) {
                    str += CosBizLogConstant.MARK_NEW_LINE + compareFields;
                }
            } catch (Exception e) {
                log.error("prefixBizLogStr error for class: {} message: {}", classThreadLocal.get().getName(),
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
