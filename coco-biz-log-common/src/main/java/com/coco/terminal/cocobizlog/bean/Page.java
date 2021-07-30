package com.coco.terminal.cocobizlog.bean;

import java.util.Map;

public class Page {
    public static final Integer DEFAULT_PAGE_NUM = 1;
    public static final Integer DEFAULT_PAGE_SIZE = 30;
    public static final String CURRENT_PAGE = "currentPage";
    public static final String PAGE_SIZE = "pageSize";
    private Integer currentPage;
    private Long startRow;
    private Integer pageSize;
    private Long totalCount;
    private Integer totalPages;

    public Page() {
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public Integer getCurrentPage() {
        if (null == this.currentPage || this.currentPage < DEFAULT_PAGE_NUM) {
            this.currentPage = DEFAULT_PAGE_NUM;
        }

        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        if (null == this.pageSize) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        }

        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalPages() {
        if (null == this.totalPages && null != this.totalCount) {
            this.totalPages = this.getTotalCount().intValue() / this.getPageSize() + (this.getTotalCount().intValue() % this.getPageSize() > 0 ? 1 : 0);
        }

        return this.totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Long getStartRow() {
        return (long) (this.getPageSize() * (this.getCurrentPage() - 1));
    }

    public void setStartRow(Long startRow) {
        this.startRow = startRow;
    }

    public static Integer pageSizeFromMap(Map map) {
        return map.containsKey("pageSize") ? Integer.valueOf(map.get("pageSize").toString()) : DEFAULT_PAGE_SIZE;
    }

    public static Integer currentPageFromMap(Map map) {
        return map.containsKey("currentPage") ? Integer.valueOf(map.get("currentPage").toString()) : DEFAULT_PAGE_NUM;
    }
}
