package com.facilio.bmsconsoleV3.context.tenantEngagement;

import com.facilio.v3.context.V3Context;

public class NewsAndInformationContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
