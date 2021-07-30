package com.coco.terminal.cocobizlog.facade;

import com.coco.terminal.cocobizlog.bean.HttpRestResult;
import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 日志自定义新增
 *
 * @author ckli01
 * @date 2019-05-21
 */
@FeignClient(name = "coco-biz-log", path = "/cocoBizLog")
public interface BizLogFacade {


    /**
     * entity 自定义日志内容 不支持搜索
     * <p>
     * entityJson:
     * key 为 LogEventFieldTypeEnum 中 bizlog_prefix 的时候 ，支持唯一主键查询
     *
     * @param logEntityDO
     * @return
     */
    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    HttpRestResult<Boolean> cocoBizLogAdd(@RequestBody LogEntityDO logEntityDO);


}