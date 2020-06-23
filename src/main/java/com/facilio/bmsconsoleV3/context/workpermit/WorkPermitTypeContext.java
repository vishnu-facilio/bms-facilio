package com.facilio.bmsconsoleV3.context.workpermit;

import com.facilio.v3.context.V3Context;

public class WorkPermitTypeContext extends V3Context {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private  String type;
    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
