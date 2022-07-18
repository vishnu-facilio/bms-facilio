package com.facilio.bmsconsoleV3.context.failurecode;

import com.facilio.v3.context.V3Context;

public class V3FailureClassContext extends V3Context {
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    private String description;
}
