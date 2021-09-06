package com.facilio.bmsconsoleV3.context.inventory;

import com.facilio.v3.context.V3Context;

public class V3ToolStatusContext extends V3Context {

    private static final long serialVersionUID = 1L;
    private String name;
    private String displayName;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
