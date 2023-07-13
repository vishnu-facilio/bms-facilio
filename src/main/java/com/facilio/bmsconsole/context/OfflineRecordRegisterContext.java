package com.facilio.bmsconsole.context;
import com.facilio.v3.context.V3Context;

public class OfflineRecordRegisterContext extends V3Context {

    private Long userMobileSettingId;
    private Long recordId;

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
 }
