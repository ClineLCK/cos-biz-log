package com.nfsq.framework.cosbizlog.test;

import com.nfsq.framework.cosbizlog.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Executor;

/**
 * @author ckli01
 * @date 2019-03-26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class HeiheiTest {

    @Autowired
    private HeiheiService heihei;

    @Autowired
    private Executor executor;

    @Test
    public void heiheiUpdateTest() throws InterruptedException {
        TestA testA = new TestA();
        testA.setRole("sjdiajidajd");
        testA.setId(11);
        testA.setName("jjijsijsijsis");

        heihei.update(testA);
        ((ThreadPoolTaskExecutor) executor).shutdown();
        ((ThreadPoolTaskExecutor) executor).setAwaitTerminationSeconds(10);
    }
}

    
    
  