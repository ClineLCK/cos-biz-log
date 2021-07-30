package com.coco.framework.cocobizlog.service;

import java.util.List;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;

/**
 * @author ckli01
 * @date 2019-09-18
 */
@Component
public class BizLogSearchService extends AbstractBizLogSearchService {

  @Override
  protected List<String> prefixSuffix(Object obj) {
    List<String> list = Lists.newArrayList();

    //    for (int i = 0; i < 500; i++) {
    //      list.add("nnnnn" + i);
    //    }
    list.add("nnnnn");

    return list;
  }
}
