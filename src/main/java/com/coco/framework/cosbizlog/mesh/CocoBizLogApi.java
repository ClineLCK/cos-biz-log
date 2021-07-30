package com.coco.framework.cosbizlog.mesh;

import com.coco.framework.cosbizlog.bean.HttpRestResult;
import com.coco.framework.cosbizlog.bean.LogEntity;
import com.coco.framework.cosbizlog.mesh.fallback.CocoBizLogApiHystrix;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 日志中心服务
 *
 * @author clinechen
 * @date 2018/10/9
 */
@FeignClient(
        name = "COCO-BIZ-LOG",
        path = "/cocoBizLog",
        fallback = CocoBizLogApiHystrix.class
)
public interface CocoBizLogApi {


    @PostMapping
    HttpRestResult<Boolean> add(@RequestBody LogEntity logEntity);

}
