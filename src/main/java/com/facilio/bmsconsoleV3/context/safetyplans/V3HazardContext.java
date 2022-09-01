package com.facilio.bmsconsoleV3.context.safetyplans;

import com.facilio.v3.context.V3Context;
import org.apache.commons.lang3.StringUtils;

public class V3HazardContext extends V3Context {
    private String name;
    private String description;
    private int type;
    private String typeEnum;
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
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getTypeEnum() {
        return typeEnum;
    }
    public void setTypeEnum(String typeEnum) {
        this.typeEnum = typeEnum;
    }
}
