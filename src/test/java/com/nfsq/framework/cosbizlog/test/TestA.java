package com.nfsq.framework.cosbizlog.test;

import com.nfsq.framework.cosbizlog.annotation.BizLogVsField;
import com.nfsq.framework.cosbizlog.bean.BizLogStr;
import lombok.Data;

/**
 * @author ckli01
 * @date 2019-03-26
 */
@Data
public class TestA extends BizLogStr {


    @BizLogVsField
    private Integer id;
    @BizLogVsField(fieldNameStr = "角色")
    private String role;
    @BizLogVsField(fieldNameStr = "姓名")
    private String name;


    @Override
    public String prefixBizLogStr() {
        return "nnnnn";
    }

    @Override
    public String suffixBizLogStr() {
        return "sssssssssss";
    }
}

    
    
  