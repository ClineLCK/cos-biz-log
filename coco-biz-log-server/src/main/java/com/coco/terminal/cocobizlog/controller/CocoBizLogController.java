package com.coco.terminal.cocobizlog.controller;

import com.alibaba.fastjson.JSONObject;
import com.coco.terminal.cocobizlog.bean.HttpRestResult;
import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import com.coco.terminal.cocobizlog.service.es.EsBaseService;
import com.coco.terminal.cocobizlog.service.mq.producer.RocketMqProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 日志提供接口
 *
 * @author ckli01
 * @date 2018/10/9
 */
@RestController
@RequestMapping("/cocoBizLog")
public class CocoBizLogController extends AbstractBaseController {

    @Autowired
    private RocketMqProducerService rocketMqProducerService;

    @Autowired
    private EsBaseService esBaseService;


    @PostMapping
    public HttpRestResult<Boolean> cocoBizLogAdd(@RequestBody Map map) {
        rocketMqProducerService.addLogEntity(map);
        return responseOK(true);
    }

    @PostMapping("/add")
    public HttpRestResult<Boolean> cocoBizLogAdd(@RequestBody @Valid LogEntityDO logEntityDO) {
        rocketMqProducerService.addLogEntity(JSONObject.parseObject(JSONObject.toJSONString(logEntityDO), Map.class));
        return responseOK(true);
    }

    @PostMapping("/initIndex")
    public HttpRestResult<Boolean> initIndex(@RequestBody List<LogEntityDO> logEntityDOS) {
        return responseOK(esBaseService.initIndex(logEntityDOS));
    }


}
