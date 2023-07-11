package com.facilio.bmsconsole.context;

import com.facilio.chain.FacilioContext;

public class OfflineRecordRegisterContext extends FacilioContext {

    private Long userMobileSettingId;
    private Long recordId;
    private Long moduleId;

    public Long getUserMobileSettingId() {
        return userMobileSettingId;
    }

    public void setUserMobileSettingId(Long userMobileSettingId) {
        this.userMobileSettingId = userMobileSettingId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }
}
