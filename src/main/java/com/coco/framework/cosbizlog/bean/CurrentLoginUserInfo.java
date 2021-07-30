package com.coco.framework.cosbizlog.bean;

import com.coco.framework.cosbizlog.util.CosBizLogSpringContext;
import com.coco.framework.cosbizlog.util.IpUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 当前登录用户信息
 *
 * @author clinechen
 * @date 2018/9/28
 */
@Getter
@Setter
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
        if (StringUtils.isEmpty(ip)) {
            HttpServletRequest httpServletRequest = CosBizLogSpringContext.getBean(HttpServletRequest.class);
            if (null != httpServletRequest) {
                ip = IpUtils.getIpAddress(httpServletRequest);
            }
        }
        return ip;
    }
}
