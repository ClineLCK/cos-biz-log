//package com.coco.terminal.cocobizlog.dal.mapper;
//
//
//import com.coco.terminal.cocobizlog.bean.LogEntityDO;
//import com.coco.terminal.cocobizlog.dal.domain.LogEntityDomain;
//import org.apache.ibatis.annotations.Param;
//
///**
// * 日志入库Mapper
// *
// * @author ckli01
// * @date 2018/8/31
// */
//public interface LogEntityMapper extends BaseMapper<LogEntityDO> {
//
//    /**
//     * 新增数据
//     * @param domain
//     */
//    void insertCurrent(LogEntityDomain domain);
//
//
//    /**
//     * 初始化表
//     * @param year
//     * @param month
//     */
//    void initTable(@Param("year") Integer year,@Param("month") Integer month);
//
//
//    /**
//     * 判断表是否 存在
//     * @param year
//     * @param month
//     * @return
//     */
//    int existTable(@Param("year") int year, @Param("month") int month);
//}
