package com.coco.framework.cocobizlog.bean;

import java.util.List;

/**
 * 列表统一返回封装
 *
 * @author ckli01
 * @date 2018/6/25
 */
public class PageList<T> extends Page {

  private List<T> data;

  public PageList() {}

  public PageList(List<T> data, Long count) {
    this.data = data;
    this.setTotalCount(count);
  }

  public List<T> getData() {
    return data;
  }

  public void setData(List<T> data) {
    this.data = data;
  }
}
