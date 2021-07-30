package com.coco.terminal.cocobizlog.service.es;

import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import com.coco.terminal.cocobizlog.bean.LogEntitySearchDTO;
import com.coco.terminal.cocobizlog.bean.PagingResult;
import com.coco.terminal.cocobizlog.entity.EsEntity;

import java.util.List;

/**
 * Es 操作基础接口
 * <p>
 * todo 优化整体操作过程
 *
 * @author ckli01
 * @date 2019-03-28
 */
public interface EsBaseService {


    /**
     * es bulk 批量添加操作
     *
     * @param esEntities
     * @return
     */
    boolean bulkAdd(List<EsEntity> esEntities);


    /**
     * es 自定义日志查询
     *
     * @param logEntitySearchDTO
     * @return
     */
    PagingResult<LogEntityDO> list(LogEntitySearchDTO logEntitySearchDTO) throws Exception;


    /**
     * es 初始化 索引
     *
     * @param logEntityDOS
     * @return
     */
    Boolean initIndex(List<LogEntityDO> logEntityDOS);
}
