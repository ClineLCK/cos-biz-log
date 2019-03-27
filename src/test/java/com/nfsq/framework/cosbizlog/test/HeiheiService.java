package com.nfsq.framework.cosbizlog.test;

import com.nfsq.framework.cosbizlog.annotation.BizLog;
import com.nfsq.framework.cosbizlog.enums.LogEventEnum;
import com.nfsq.framework.cosbizlog.enums.LogModuleEnum;
import com.nfsq.framework.cosbizlog.service.BizLogBaseBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 测试类
 *
 * @author ckli01
 * @date 2019-03-26
 */
@Component
@Slf4j
public class HeiheiService  implements BizLogBaseBizService<TestA> {


    @BizLog(logModule = LogModuleEnum.DEFAULT, logEvent = LogEventEnum.ADD)
    public String add(String s) {
        System.out.println("heihei");
        return s;
    }


    @BizLog(moduleEnumName = "CUST_SHIPPING_ADDR", eventEnumName="ADD")
    public String update(TestA s) {
        log.info("update do dodododdo...");
        return "sss";
    }


    @Override
    public TestA getById(Object id) throws Exception {
        TestA testA = new TestA();
        testA.setId(2);
        testA.setName("2312312");
        testA.setRole("sjsijsiis");
        return testA;
    }


}

    
    
  