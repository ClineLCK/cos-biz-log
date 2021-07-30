package com.coco.framework.cocobizlog.test;

import com.coco.framework.cocobizlog.core.BizLogStr;
import com.coco.framework.cocobizlog.core.annotation.BizLogVsField;
import lombok.Data;

/**
 * @author ckli01
 * @date 2019-09-18
 */
@Data
public class TestB extends BizLogStr {

  private Integer id;

  @BizLogVsField(fieldNameStr = "B性别")
  private String sex;

  @BizLogVsField(fieldNameStr = "B年龄")
  private String age;

  @BizLogVsField(fieldNameStr = "Bxixi")
  private String xixi;

  private Boolean soso;

  private boolean isOo;

  @Override
  public Object cocoKey() {
    return id;
  }
}
