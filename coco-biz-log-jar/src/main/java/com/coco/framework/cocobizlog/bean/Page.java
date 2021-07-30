package com.coco.framework.cocobizlog.bean;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 前端请求分页类
 *
 * @author ckli01
 * @date 2018/6/25
 */
@Slf4j
public class Page {

  /** 默认页面开始页码 */
  public static final Integer DEFAULT_PAGE_NUM = 1;

  /** 默认页面显示条数 */
  public static final Integer DEFAULT_PAGE_SIZE = 30;

  public static final String CURRENT_PAGE = "currentPage";
  public static final String PAGE_SIZE = "pageSize";

  /** 当前页 */
  private Integer currentPage;

  /** 起始位置 */
  private Long startRow;

  /** 页面显示条数 */
  private Integer pageSize = DEFAULT_PAGE_SIZE;

  /** 总条数 */
  private Long totalCount;

  /** 总页数 */
  private Integer totalPages;

  public static Integer pageSizeFromMap(Map map) {
    return map.containsKey(PAGE_SIZE)
        ? Integer.valueOf(map.get(PAGE_SIZE).toString())
        : DEFAULT_PAGE_SIZE;
  }

  public static Integer currentPageFromMap(Map map) {
    return map.containsKey(CURRENT_PAGE)
        ? Integer.valueOf(map.get(CURRENT_PAGE).toString())
        : DEFAULT_PAGE_NUM;
  }

  public Integer getCurrentPage() {
    if (null == this.currentPage || this.currentPage < DEFAULT_PAGE_NUM) {
      this.currentPage = DEFAULT_PAGE_NUM;
    }
    return currentPage;
  }

  public void setCurrentPage(Integer currentPage) {
    this.currentPage = currentPage;
  }

  public Integer getPageSize() {
    if (null == this.pageSize) {
      this.pageSize = DEFAULT_PAGE_SIZE;
    }
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Long getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Long totalCount) {
    this.totalCount = totalCount;
  }

  public Integer getTotalPages() {
    if (null == this.totalPages && null != totalCount) {
      totalPages =
          getTotalCount().intValue() / getPageSize()
              + (getTotalCount().intValue() % getPageSize() > 0 ? 1 : 0);
    }
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public Long getStartRow() {
    return Long.valueOf(getPageSize() * (getCurrentPage() - 1));
  }

  public void setStartRow(Long startRow) {
    this.startRow = startRow;
  }
}
