package com.facilio.bmsconsole.context;

public class PortalCommentsSharingContext {
    private long appId = -1;

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public Boolean getAppSelected() {
        return isAppSelected;
    }

    public void setAppSelected(Boolean appSelected) {
        isAppSelected = appSelected;
    }

    private Boolean isAppSelected = false;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    private String appName=null;
}
