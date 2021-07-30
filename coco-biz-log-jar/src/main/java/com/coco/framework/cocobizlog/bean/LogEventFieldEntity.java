package com.coco.framework.cocobizlog.bean;

import com.coco.framework.cocobizlog.common.CosBizLogConstant;
import com.coco.framework.cocobizlog.core.enums.LogEventFieldTypeEnum;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 日志 事件 字段记录 实体类
 *
 * @author ckli01
 * @date 2019-05-21
 */
@Data
public class LogEventFieldEntity {

  /** 字段 */
  private String filedName;

  /** 字段中文名 */
  private String filedNameZh;

  /** 结果 */
  private String result;

  /** 字段类型 */
  private LogEventFieldTypeEnum logEventFieldTypeEnum;

  /**
   * 结构化字符串输出
   *
   * @return
   */
  public String formaterFieldStrZh() {
    return new StringBuilder()
        .append(StringUtils.isEmpty(filedNameZh) ? "" : filedNameZh)
        .append(StringUtils.isEmpty(filedNameZh) ? "" : ": ")
        .append(result)
        .append(CosBizLogConstant.MARK_NEW_LINE)
        .toString();
  }

  public String getFiledName() {
    if (StringUtils.isEmpty(filedName)) {
      if (logEventFieldTypeEnum != null) {
        return logEventFieldTypeEnum.getFiledName();
      }
    }
    return filedName;
  }
}
