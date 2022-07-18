package com.facilio.bmsconsoleV3.context.failurecode;

import com.facilio.v3.context.V3Context;

public class V3FailureCodeContext extends V3Context {
    private String code;
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    private String description;
}
