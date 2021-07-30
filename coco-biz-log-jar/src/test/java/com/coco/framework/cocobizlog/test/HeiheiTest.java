package com.coco.framework.cocobizlog.test;

import com.coco.framework.cocobizlog.service.BizLogSearchService;
import com.coco.framework.cocobizlog.Application;
import com.coco.framework.cocobizlog.core.BizLogIniter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import com.coco.terminal.cocobizlog.bean.LogEntityBaseSearchDTO;
import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import com.coco.terminal.cocobizlog.bean.LogEntitySearchDTO;
import com.coco.terminal.cocobizlog.bean.PagingResult;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author ckli01
 * @date 2019-03-26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class HeiheiTest {

  @Autowired
  private HeiheiService heiheiService;

  @Autowired
  private Executor executor;

  @Autowired
  private BizLogSearchService bizLogSearchService;


  @Test
  public void heiheiUpdateTest() throws InterruptedException {
    TestA testA = new TestA();
    testA.setRole("sdsa");
    testA.setId(10);
    testA.setName("jjijsijsijsis");

    List<TestB> testBS = new ArrayList<>();

    TestB testB = new TestB();
    testB.setId(12);
    testB.setAge("1212");
    testB.setSex("sasa");
    testBS.add(testB);

    testB = new TestB();
    testB.setId(13);
    testB.setAge("1313");
    testB.setSex("sdsd");
    testB.setXixi("33");
    testBS.add(testB);

    testA.setTestBs(testBS);

    heiheiService.update(testA);
    System.out.println(1);

    //        ((ThreadPoolTaskExecutor) executor).shutdown();
    //        ((ThreadPoolTaskExecutor) executor).setAwaitTerminationSeconds(10);
    Thread.currentThread().join();
  }

  @Test
  public void heiheiBatchUpdateTest1() throws InterruptedException {
    List<TestA> testAS = new ArrayList<>();

    TestA testA = new TestA();
    testA.setRole("sisisisi");
    testA.setId(2);
    testA.setName("jijijiji");

    testAS.add(testA);

    testA = new TestA();
    testA.setRole("ioioioi");
    testA.setId(3);
    testA.setName("sjsjsjsj");

    testAS.add(testA);

    BizLogIniter logIniter = new BizLogIniter();

//    heiheiService.batchUpdate1(testAS);

    //        ((ThreadPoolTaskExecutor) executor).shutdown();
    //        ((ThreadPoolTaskExecutor) executor).setAwaitTerminationSeconds(10);
    Thread.currentThread().join();
  }

  @Test
  public void heiheiBatchUpdateTest2() throws InterruptedException {

    TestA testA = new TestA();
    testA.setRole("sisisisi");
    testA.setName("jijijiji");

    List<Integer> list = new ArrayList<>();
    list.add(2);
    list.add(3);
    testA.setIds(list);

    heiheiService.batchUpdate2(testA);

    //        ((ThreadPoolTaskExecutor) executor).shutdown();
    //        ((ThreadPoolTaskExecutor) executor).setAwaitTerminationSeconds(10);
    Thread.currentThread().join();
  }

  @Test
  public void heiheiAddTest() throws InterruptedException {

    TestA testA = new TestA();

    AtomicInteger integer = new AtomicInteger(0);

    for (int i = 0; i < 23; i++) {
      testA.setRole(UUID.randomUUID().toString());
      testA.setId(Long.valueOf(System.nanoTime()).intValue());
      testA.setName(UUID.randomUUID().toString());

      heiheiService.add(testA);
      System.out.println(i);
    }

    //        ((ThreadPoolTaskExecutor) executor).shutdown();
    //        ((ThreadPoolTaskExecutor) executor).setAwaitTerminationSeconds(10);
    Thread.currentThread().join();
  }

  @Test
  public void bizLogSearchFieldMapTest() {
    Map<String, String> map = bizLogSearchService.fieldsMap(TestA.class);

    System.out.println(1);
  }

  @Test
  public void batchDeleteTest() {
    String s = heiheiService.delete(Lists.newArrayList("1", "2", "3"));

    System.out.println(1);
  }

  @Test
  public void deleteTest() {
    String s = heiheiService.delete("2222");

    System.out.println(1);
  }


  @Test
  public void fieldsMapForFieldNameZhTest() {
    Map<String, String> map = bizLogSearchService.fieldsMapForFieldNameZh(TestA.class);

    System.out.println(1);
  }

  @Test
  public void bizLogSearchTest() {
    LogEntitySearchDTO logEntitySearchDTO = new LogEntitySearchDTO();

    LogEntityBaseSearchDTO logEntityBaseSearchDTO = new LogEntityBaseSearchDTO();
    logEntitySearchDTO.setBaseSearchDTO(logEntityBaseSearchDTO);
    logEntityBaseSearchDTO.setEvent(1);
    logEntityBaseSearchDTO.setModule(4);
    logEntityBaseSearchDTO.setServiceName("lck-test-cos-biz-log");

    Map<String, String> map = new HashMap<>();
    map.put("nested-testBs.age", "");

    PagingResult<LogEntityDO> pageList = bizLogSearchService.search(logEntitySearchDTO, null, map);

    for (int i = pageList.getPageIndex() + 1; i <= pageList.getPageTotal(); i++) {
      logEntitySearchDTO.setCurrentPage(i);
      pageList = bizLogSearchService.search(logEntitySearchDTO, null, map);
      System.out.println(1);
    }

    System.out.println(1);
  }
}
