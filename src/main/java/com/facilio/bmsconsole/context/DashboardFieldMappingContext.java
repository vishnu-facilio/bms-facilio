package com.facilio.bmsconsole.context;

import com.facilio.chain.FacilioContext;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DashboardFieldMappingContext extends FacilioContext {
    private long id;
    private long fieldId;
    private long moduleId;
    private long orgId;
    private long dashboardUserFilterId;
    private boolean isDefault;
}
