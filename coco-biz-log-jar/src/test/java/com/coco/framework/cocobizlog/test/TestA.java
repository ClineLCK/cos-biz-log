package com.coco.framework.cocobizlog.test;

import com.coco.framework.cocobizlog.core.BizLogStr;
import com.coco.framework.cocobizlog.core.annotation.BizLogBatchUpdateKey;
import com.coco.framework.cocobizlog.core.annotation.BizLogVsField;
import java.util.List;
import lombok.Data;

/**
 * @author ckli01
 * @date 2019-03-26
 */
@Data
public class TestA extends BizLogStr {

  private Integer id;

  @BizLogVsField(fieldNameStr = "角色")
  private String role;

  @BizLogVsField(fieldNameStr = "姓名")
  private String name;

  private Boolean soso;

  private boolean isOo;

  @BizLogBatchUpdateKey
  private List<Integer> ids;

  @BizLogVsField(fieldNameStr = "testB")
  private TestB testB;

  @BizLogVsField(fieldNameStr = "哈哈哈哈",childExtend = false)
  private List<TestB> testBs;

  @BizLogVsField(fieldNameStr = "enen")
  private List<String> enen;


  @Override
  public Object cocoKey() {
    return "nnnnn";
  }
}
