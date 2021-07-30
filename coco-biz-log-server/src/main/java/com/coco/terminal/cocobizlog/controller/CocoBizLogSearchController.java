package com.coco.terminal.cocobizlog.controller;

import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import com.coco.terminal.cocobizlog.bean.LogEntitySearchDTO;
import com.coco.terminal.cocobizlog.bean.PagingResult;
import com.coco.terminal.cocobizlog.service.es.impl.BizLogEsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供查询类
 *
 * @author ckli01
 * @date 2019-09-03
 */
@RestController
@RequestMapping("/cocoBizLogSearch")
@Slf4j
public class CocoBizLogSearchController extends AbstractBaseController {


    @Autowired
    private BizLogEsServiceImpl BizLogEsService;


    @PostMapping
    public PagingResult<LogEntityDO> list(@RequestBody LogEntitySearchDTO logEntitySearchDTO) {
        PagingResult<LogEntityDO> pagingResult;
        try {
            pagingResult = BizLogEsService.list(logEntitySearchDTO);
            pagingResult.setSuccess(true);
        } catch (Exception e) {
            pagingResult = new PagingResult<>();
            if (e instanceof ElasticsearchStatusException) {
                ElasticsearchStatusException exception = (ElasticsearchStatusException) e;
                if (RestStatus.NOT_FOUND.equals(exception.status())) {
                    pagingResult.setSuccess(true);
                }
            }
            pagingResult.setMessage(e.getMessage());
            log.error("CocoBizLogSearchController list error : {}", e.getMessage(), e);
        }
        return pagingResult;
    }


}

    
    
  