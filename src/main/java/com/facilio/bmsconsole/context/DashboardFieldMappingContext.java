package com.facilio.bmsconsole.context;

import com.facilio.chain.FacilioContext;

public class DashboardFieldMappingContext extends FacilioContext {
    private long id;
    public long getId() { return id;}
    public void setId(long id) { this.id = id; }

    private long fieldId;
    public long getFieldId() { return fieldId;}
    public void setFieldId(long fieldId) {this.fieldId = fieldId;}

    private long moduleId;
    public long getModuleId() { return moduleId;}
    public void setModuleId(long moduleId) {this.moduleId = moduleId;}

    private long orgId;
    public long getOrgId() {return orgId;}
    public void setOrgId(long orgId) {this.orgId = orgId;}

    private long dashboardUserFilterId;
    public long getDashboardUserFilterId() {return dashboardUserFilterId;}
    public void setDashboardUserFilterId(long dashboardUserFilterId) {this.dashboardUserFilterId = dashboardUserFilterId;}

    private boolean isDefault;
    public boolean getIsDefault() { return  isDefault;}
    public void setIsDefault(boolean isDefault) {this.isDefault = isDefault;}
}
