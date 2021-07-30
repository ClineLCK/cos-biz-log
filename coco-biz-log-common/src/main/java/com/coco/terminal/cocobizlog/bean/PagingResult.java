package com.coco.terminal.cocobizlog.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @Descriptions: 数据分页结果
 */
public class PagingResult<TItem> extends HttpRestResult<List<TItem>> implements Serializable {

    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 当前页码
     */
    private int pageIndex;

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 总条目数
     */
    private long total;

    /**
     * 总页码
     */
    private long pageTotal;


    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPageTotal() {
        return (total + pageSize - 1) / pageSize;
    }


    public PagingResult() {
        this.pageIndex = 1;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    public PagingResult(int pageIndex, int pageSize) {
        if (pageIndex < 1) {
            pageIndex = 1;
        }
        if (pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    /**
     * 设置分页数据
     * 会自动设置成功及其code
     *
     * @param total 数据总条目数
     * @param rows  本页数据集合
     */
    public void setPageData(Long total, List<TItem> rows) {
        this.total = total;
        this.setData(rows);
    }

    @Override
    public String toString() {
        return "PagingResult{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                ", total=" + total +
                ", pageTotal=" + this.getPageTotal() +
                '}';
    }
}
