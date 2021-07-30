package com.coco.terminal.cocobizlog.enums;

import lombok.Getter;

/**
 * 字段  类型 枚举
 *
 * @author ckli01
 * @date 2019-09-26
 */
@Getter
public enum LogFieldQueryTypeEnum {


  /**
   * list 查询
   */
  LIST("nested-", "list"),

  ;


  private String type;

  private String desc;


  LogFieldQueryTypeEnum(String type, String desc) {
    this.type = type;
    this.desc = desc;
  }




}
