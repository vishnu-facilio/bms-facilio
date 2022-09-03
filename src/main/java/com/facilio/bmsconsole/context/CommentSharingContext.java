package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class CommentSharingContext extends ModuleBaseWithCustomFields {

    private static final long serialVersionUID = 1L;

    private long parentId = -1;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getParentModuleId() {
        return parentModuleId;
    }

    public void setParentModuleId(long parentModuleId) {
        this.parentModuleId = parentModuleId;
    }

    private long parentModuleId = -1;

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    private long appId=-1;

}
