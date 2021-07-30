package com.coco.framework.cocobizlog.test;

import com.coco.framework.cocobizlog.core.annotation.BizLog;
import com.coco.framework.cocobizlog.core.enums.LogEventEnum;
import com.coco.framework.cocobizlog.core.enums.LogModuleEnum;
import com.coco.framework.cocobizlog.service.BizLogBaseBizService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class HeiheiService implements BizLogBaseBizService {

  @BizLog(moduleEnumName = "CUST_SHIPPING_ADDR", logEvent = LogEventEnum.ADD)
  public String add(TestA testAS) {
    System.out.println("heihei");
    return "hihihihihih";
  }

  @BizLog(moduleEnumName = "CUST_SHIPPING_ADDR", eventEnumName = "UPDATE")
  public String update(TestA s) {
    log.info("update do dodododdo...");
    return "sss";
  }

  @BizLog(logModule = LogModuleEnum.DEFAULT, logEvent = LogEventEnum.BATCH_UPDATE)
  public String batchUpdate1(List<TestA> testAS) {
    System.out.println("heihei");
    return "sadsad";
  }

  @BizLog(logModule = LogModuleEnum.DEFAULT, logEvent = LogEventEnum.BATCH_UPDATE)
  public String batchUpdate2(TestA testAS) {
    System.out.println("heihei");
    return "sadsad";
  }


  @BizLog(logModule = LogModuleEnum.DEFAULT, logEvent = LogEventEnum.BATCH_DELETE)
  public String delete(List<String> codes) {
    System.out.println("batch delete");
    return "batch delete";
  }

  @BizLog(logModule = LogModuleEnum.DEFAULT, logEvent = LogEventEnum.DELETE)
  public String delete(String code) {
    System.out.println("delete");
    return "delete";
  }


  @Override
  public TestA getPrefixEntityById(Object id) throws Exception {
    TestA testA = new TestA();
    testA.setId(10);
    testA.setName("13");
    testA.setRole("qw");

    List<TestB> testBS = new ArrayList<>();

    TestB testB = new TestB();
    testB.setId(12);
    testB.setAge("3434");
    testB.setSex("erer");
    testBS.add(testB);

    testB = new TestB();
    testB.setId(13);
    testB.setAge("3535");
    testB.setSex("wewe");
    testBS.add(testB);

    testA.setTestBs(testBS);

    return testA;
  }

  @Override
  public List getPrefixEntityByIds(List ids) throws Exception {
    List<TestA> testAS = new ArrayList<>();

    TestA testA = new TestA();
    testA.setId(2);
    testA.setName("2222222");
    testA.setRole("222role");

    testAS.add(testA);

    testA = new TestA();
    testA.setId(3);
    testA.setName("33333");
    testA.setRole("333role");
    testAS.add(testA);

    return testAS;
  }

  @Override
  public Map<String, String> getSearchEntity(Object obj) throws Exception {
    return null;
  }
}
