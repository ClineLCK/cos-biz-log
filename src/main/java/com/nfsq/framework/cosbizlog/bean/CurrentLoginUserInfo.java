package com.nfsq.framework.cosbizlog.bean;

import com.nfsq.framework.cosbizlog.util.CosBizLogSpringContext;
import com.nfsq.framework.cosbizlog.util.IpUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 用户名称
     */
    private String name;
    /**
     * 用户id
     */
    private Long id;

    /**
     * 登录ip
     */
    private String ip;

    public String getIp() {
        try {
            if (StringUtils.isEmpty(ip)) {
                HttpServletRequest httpServletRequest = CosBizLogSpringContext.getBean(HttpServletRequest.class);
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
