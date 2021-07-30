package com.coco.terminal.cocobizlog.dal.domain;


import com.coco.terminal.cocobizlog.bean.LogEntityDO;

/**
 * @author ckli01
 * @date 2019/11/25
 */
public class LogEntityDomain extends LogEntityDO {


    private Integer year;

    private Integer month;


    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
}

    
    
  