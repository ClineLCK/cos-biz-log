package com.coco.framework.cocobizlog.bean;

import com.coco.framework.cocobizlog.util.CosBizLogSpringContext;
import com.coco.framework.cocobizlog.util.IpUtils;

import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 当前登录用户信息
 *
 * @author ckli01
 * @date 2018/9/28
 */
@Getter
@Setter
@Slf4j
public class CurrentLoginUserInfo {

  /** 用户名称 */
  private String name;
  /** 用户id */
  private String id;

  /** 登录ip */
  private String ip;

  public String getIp() {
    try {
      if (StringUtils.isEmpty(ip)) {
        HttpServletRequest httpServletRequest =
            CosBizLogSpringContext.getBean(HttpServletRequest.class);
        if (null != httpServletRequest) {
          ip = IpUtils.getIpAddress(httpServletRequest);
        }
      }
    } catch (Exception e) {
      log.warn("BizLogAop getIp error : {}", e.getMessage());
    }
    return ip;
  }
}
