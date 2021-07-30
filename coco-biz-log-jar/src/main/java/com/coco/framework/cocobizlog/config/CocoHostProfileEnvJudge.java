package com.coco.framework.cocobizlog.config;

import com.coco.framework.cocobizlog.util.CosBizLogSpringContext;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

/**
 * 日志中心主机地址在配置环境 变量 选取
 *
 * @author ckli01
 * @date 2019-04-01
 */
@Slf4j
public class CocoHostProfileEnvJudge {

  private static final String PROPERTIES_FILE_PATH = "coco.properties";
  private static AtomicInteger atomicInteger;
  private static String[] hosts;

  static {
    init();
  }

  /** 日志处理中心地址初始化 若环境变量中配置 coco.host 以此优先 否则已配置文件coco.properties 中配置对应各自环境做选择 */
  public static void init() {
    atomicInteger = new AtomicInteger(0);

    try {
      ConfigurableEnvironment environment =
          CosBizLogSpringContext.getBean(ConfigurableEnvironment.class);
      String hostStr = environment.getProperty("coco.host");
      if (StringUtils.isEmpty(hostStr)) {
        Properties properties =
            PropertiesLoaderUtils.loadProperties(new ClassPathResource(PROPERTIES_FILE_PATH));
        // spring boot 应用
        String springProfile = environment.getProperty("spring.profiles.active");

        if (prod(springProfile)) {
          hostStr = properties.getProperty("coco.prod.host");
        } else if (test(springProfile)) {
          hostStr = properties.getProperty("coco.test.host");
        } else if (dev(springProfile)) {
          hostStr = properties.getProperty("coco.dev.host");
        } else {
          hostStr = properties.getProperty("coco.default.host");
        }
      }
      hosts = hostStr.split(";");
      // todo ssm 支持

    } catch (Exception e) {
      log.warn("CocoHostProfileEnvJudge init error for {}", e.getMessage());
    }
  }

  /**
   * 轮询负载均衡
   *
   * @return
   */
  public static String getHost() {
    String host;

    if (hosts.length > 0) {
      int i = atomicInteger.getAndAdd(1);
      host = hosts[i % hosts.length];
      if (i > 10000) {
        atomicInteger.set(0);
      }
    } else {
      host = "";
    }

    return host;
  }

  /**
   * 测试环境
   *
   * @param profile
   * @return
   */
  private static boolean test(String profile) {
    if ("test".equalsIgnoreCase(profile)) {
      return true;
    } else if ("fat".equalsIgnoreCase(profile)) {
      return true;
    } else if ("daily".equalsIgnoreCase(profile)) {
      return true;
    } else if ("t".equalsIgnoreCase(profile)) {
      return true;
    }
    return false;
  }

  /**
   * 开发环境 默认
   *
   * @param profile
   * @return
   */
  private static boolean dev(String profile) {
    if ("dev".equalsIgnoreCase(profile)) {
      return true;
    } else if ("d".equalsIgnoreCase(profile)) {
      return true;
    }
    return false;
  }

  /**
   * 生产环境
   *
   * @param profile
   * @return
   */
  private static boolean prod(String profile) {
    if ("prod".equalsIgnoreCase(profile)) {
      return true;
    } else if ("pro".equalsIgnoreCase(profile)) {
      return true;
    } else if ("p".equalsIgnoreCase(profile)) {
      return true;
    }
    return false;
  }
}
