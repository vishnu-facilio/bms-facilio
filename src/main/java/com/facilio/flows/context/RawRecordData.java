package com.facilio.flows.context;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@Getter
@Setter
public class RawRecordData implements Serializable {
    private String fieldName;

    public void setValueStr(String valueStr) {
        if(StringUtils.isNotEmpty(valueStr) && valueStr.length()>1000){
            throw new IllegalArgumentException("value's length can not be more than 1000 characters for fieldName:"+fieldName);
        }
        this.valueStr = valueStr;
    }

    private long fieldId=-1l;
    private long blockId=-1l;
    private String valueStr;
}
