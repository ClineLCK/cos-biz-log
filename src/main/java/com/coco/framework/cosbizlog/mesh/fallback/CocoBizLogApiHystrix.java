package com.coco.framework.cosbizlog.mesh.fallback;

import com.coco.framework.cosbizlog.bean.HttpRestResult;
import com.coco.framework.cosbizlog.bean.LogEntity;
import com.coco.framework.cosbizlog.mesh.CocoBizLogApi;
import org.springframework.stereotype.Component;

/**
 * 日志服务中心 回滚熔断器
 *
 * @author clinechen
 * @date 2018/10/12
 */
@Component
public class CocoBizLogApiHystrix implements CocoBizLogApi {

    @Override
    public HttpRestResult<Boolean> add(LogEntity logEntity) {
        HttpRestResult<Boolean> restResult = new HttpRestResult<>();
        restResult.setMessage("CocoBizLogApiHystrix do the Hystrix job");
        return restResult;
    }
}
