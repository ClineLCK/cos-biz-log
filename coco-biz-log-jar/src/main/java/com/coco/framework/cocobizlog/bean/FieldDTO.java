package com.coco.framework.cocobizlog.bean;

import lombok.Data;

/**
 * 字段类型 属性 传递类
 *
 * @author ckli01
 * @date 2019-09-25
 */
@Data
public class FieldDTO {

  /**
   * 中文显示
   */
  private String fieldNameZh;

  /**
   * 英文显示
   */
  private String fieldNameEn;

  /**
   * List 值 前面是否需要前缀
   */
  private Boolean prefixForList = false;

  /**
   * 值前面的描述 内容是否用点（.） 分隔
   */
  private Boolean keyPrefixWithPoint = false;

  public FieldDTO() {
  }

  public FieldDTO(String fieldNameZh, String fieldNameEn, Boolean prefixForList) {
    this.fieldNameZh = fieldNameZh;
    this.fieldNameEn = fieldNameEn;
    this.prefixForList = prefixForList;
  }
}
