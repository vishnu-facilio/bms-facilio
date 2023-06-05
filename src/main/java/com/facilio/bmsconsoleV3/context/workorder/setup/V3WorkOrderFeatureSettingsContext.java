package com.facilio.bmsconsoleV3.context.workorder.setup;

import com.facilio.modules.FacilioStatus;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V3WorkOrderFeatureSettingsContext extends V3Context {

    private Integer settingType;
    public void setSettingType(Integer settingType) {
        this.settingType = settingType;
        if(settingType != null){
            settingTypeEnum = WorkOrderFeatureSettingType.valueOf(settingType);
        }
    }

    private Long allowedTicketStatusId;
    private long lastModifiedTime;
    private long sysModifiedById;

    // these 2 objects has to be set from property settingType, allowedTicketStatusId
    private WorkOrderFeatureSettingType settingTypeEnum;
    private FacilioStatus allowedTicketStatus;
    private Boolean isAllowed;
}
