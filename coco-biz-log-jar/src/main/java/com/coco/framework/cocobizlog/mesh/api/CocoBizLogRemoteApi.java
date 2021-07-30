package com.coco.framework.cocobizlog.mesh.api;

import com.coco.framework.cocobizlog.bean.LogEntity;
import com.coco.terminal.cocobizlog.bean.HttpRestResult;
import com.coco.terminal.cocobizlog.bean.LogEntityDO;
import com.coco.terminal.cocobizlog.bean.LogEntitySearchDTO;
import com.coco.terminal.cocobizlog.bean.PagingResult;
import java.util.List;

/**
 * 发送日志到日志中心接口
 *
 * @author ckli01
 * @date 2019-04-01
 */
public interface CocoBizLogRemoteApi {

  /**
   * 添加
   *
   * @param logEntity
   * @returnø
   */
  HttpRestResult<Boolean> add(LogEntity logEntity);

  /**
   * 查询
   *
   * @param logEntitySearchDTO
   * @return
   */
  PagingResult<LogEntityDO> search(LogEntitySearchDTO logEntitySearchDTO);


  /**
   * 初始化索引
   *
   * @param list
   * @return
   */
  HttpRestResult<Boolean> initIndex(List<LogEntityDO> list);


}
