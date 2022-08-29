package com.facilio.bmsconsoleV3.context.safetyplans;

import com.facilio.v3.context.V3Context;
import org.apache.commons.lang3.StringUtils;

public class V3PrecautionContext extends V3Context {

    private String name;
    private String description;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = StringUtils.upperCase(name);
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
