package com.coco.terminal.cocobizlog.facade;

import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import com.coco.terminal.cocobizlog.bean.LogEntitySearchDTO;
import com.coco.terminal.cocobizlog.bean.PagingResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 日志 查询
 *
 * @author ckli01
 * @date 2019/11/15
 */
@FeignClient(name = "coco-biz-log", path = "/cocoBizLogSearch")
public interface BizLogSearchFacade {


    /**
     * 查询 ：
     * baseSearchDTO 里面属性 与 模块 应用相关的 必填
     * prefixSuffix 为展示的主键 可选
     *
     * @param logEntitySearchDTO
     * @return
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    PagingResult<LogEntityDO> list(@RequestBody LogEntitySearchDTO logEntitySearchDTO);

}
