package com.facilio.bmsconsoleV3.actions;

import com.facilio.v3.V3Action;

public class SetupAction extends V3Action {
    private String url;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String execute() throws Exception {

        return SUCCESS;
    }
}
