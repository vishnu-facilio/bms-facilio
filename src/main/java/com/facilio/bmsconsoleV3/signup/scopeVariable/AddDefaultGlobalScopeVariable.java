package com.facilio.bmsconsoleV3.signup.scopeVariable;

import com.facilio.beans.GlobalScopeBean;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ValueGeneratorBean;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddDefaultGlobalScopeVariable extends SignUpData {
    @Override
    public void addData() throws Exception {
        GlobalScopeBean scopeBean = (GlobalScopeBean) BeanFactory.lookup("ScopeBean");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        ValueGeneratorBean valGenBean = (ValueGeneratorBean) BeanFactory.lookup("ValueGeneratorBean");

        //Maintenance app - site
        GlobalScopeVariableContext maintenanceSiteScopeVariable = new GlobalScopeVariableContext();
        maintenanceSiteScopeVariable.setLinkName("default_maintenance_site");
        maintenanceSiteScopeVariable.setApplicableModuleId(modBean.getModule(FacilioConstants.ContextNames.SITE).getModuleId());
        maintenanceSiteScopeVariable.setStatus(true);
        maintenanceSiteScopeVariable.setShowSwitch(true);
        maintenanceSiteScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        maintenanceSiteScopeVariable.setDescription("Default Site Scope Variable");
        maintenanceSiteScopeVariable.setDisplayName("Site Scope Variable");
        maintenanceSiteScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
        maintenanceSiteScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.SiteValueGenerator").getId());
        scopeBean.addScopeVariable(maintenanceSiteScopeVariable);

        //Occupant app - user
        GlobalScopeVariableContext occupantUserScopeVariable = new GlobalScopeVariableContext();
        occupantUserScopeVariable.setLinkName("default_occupant_user");
        occupantUserScopeVariable.setApplicableModuleName(FacilioConstants.ContextNames.USERS);
        occupantUserScopeVariable.setStatus(true);
        occupantUserScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));
        occupantUserScopeVariable.setDescription("Default User Scope Variable");
        occupantUserScopeVariable.setDisplayName("User Scope Variable");
        occupantUserScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
        occupantUserScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.OrgUserValueGenerator").getId());
        scopeBean.addScopeVariable(occupantUserScopeVariable);

        //Tenant app - tenant
        GlobalScopeVariableContext tenantUserScopeVariable = new GlobalScopeVariableContext();
        tenantUserScopeVariable.setLinkName("default_tenant_user");
        tenantUserScopeVariable.setApplicableModuleId(modBean.getModule(FacilioConstants.ContextNames.TENANT).getModuleId());
        tenantUserScopeVariable.setStatus(true);
        tenantUserScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        tenantUserScopeVariable.setDescription("Default User Scope Variable");
        tenantUserScopeVariable.setDisplayName("User Scope Variable");
        tenantUserScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
        tenantUserScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.TenantValueGenerator").getId());
        scopeBean.addScopeVariable(tenantUserScopeVariable);

        //Vendor app - vendor
        GlobalScopeVariableContext vendorUserScopeVariable = new GlobalScopeVariableContext();
        vendorUserScopeVariable.setLinkName("default_vendor_user");
        vendorUserScopeVariable.setApplicableModuleId(modBean.getModule(FacilioConstants.ContextNames.VENDORS).getModuleId());
        vendorUserScopeVariable.setStatus(true);
        vendorUserScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP));
        vendorUserScopeVariable.setDescription("Default User Scope Variable");
        vendorUserScopeVariable.setDisplayName("User Scope Variable");
        vendorUserScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
        vendorUserScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.VendorValueGenerator").getId());
        scopeBean.addScopeVariable(vendorUserScopeVariable);

        //Maintenance app - storeroom
        GlobalScopeVariableContext maintenanceStoreroomScopeVariable = new GlobalScopeVariableContext();
        maintenanceStoreroomScopeVariable.setLinkName("default_maintenance_storeroom");
        maintenanceStoreroomScopeVariable.setApplicableModuleId(modBean.getModule(FacilioConstants.ContextNames.STORE_ROOM).getModuleId());
        maintenanceStoreroomScopeVariable.setStatus(true);
        maintenanceStoreroomScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        maintenanceStoreroomScopeVariable.setDescription("Default Storeroom Scope Variable");
        maintenanceStoreroomScopeVariable.setDisplayName("Storeroom Scope Variable");
        maintenanceStoreroomScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
        maintenanceStoreroomScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.StoreRoomValueGenerator").getId());
        scopeBean.addScopeVariable(maintenanceStoreroomScopeVariable);

        //Maintenance app - user
        GlobalScopeVariableContext maintenanceUserScopeVariable = new GlobalScopeVariableContext();
        maintenanceUserScopeVariable.setLinkName("default_maintenance_user");
        maintenanceUserScopeVariable.setApplicableModuleName(FacilioConstants.ContextNames.USERS);
        maintenanceUserScopeVariable.setStatus(true);
        maintenanceUserScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));
        maintenanceUserScopeVariable.setDescription("Default Users Scope Variable");
        maintenanceUserScopeVariable.setDisplayName("Users Scope Variable");
        maintenanceUserScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
        maintenanceUserScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.OrgUserValueGenerator").getId());
        scopeBean.addScopeVariable(maintenanceUserScopeVariable);

        //Tenant app - people
        GlobalScopeVariableContext tenantPeopleScopeVariable = new GlobalScopeVariableContext();
        tenantPeopleScopeVariable.setLinkName("default_tenant_people");
        tenantPeopleScopeVariable.setApplicableModuleId(modBean.getModule(FacilioConstants.ContextNames.PEOPLE).getModuleId());
        tenantPeopleScopeVariable.setStatus(true);
        tenantPeopleScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        tenantPeopleScopeVariable.setDescription("Default People Scope Variable");
        tenantPeopleScopeVariable.setDisplayName("People Scope Variable");
        tenantPeopleScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
        tenantPeopleScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.PeopleValueGenerator").getId());
        scopeBean.addScopeVariable(tenantPeopleScopeVariable);

        //Tenant app - audience
        GlobalScopeVariableContext tenantAudienceScopeVariable = new GlobalScopeVariableContext();
        tenantAudienceScopeVariable.setLinkName("default_tenant_audience");
        tenantAudienceScopeVariable.setApplicableModuleId(modBean.getModule(FacilioConstants.ContextNames.AUDIENCE).getModuleId());
        tenantAudienceScopeVariable.setStatus(true);
        tenantAudienceScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        tenantAudienceScopeVariable.setDescription("Default Audience Scope Variable");
        tenantAudienceScopeVariable.setDisplayName("Audience Scope Variable");
        tenantAudienceScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
        tenantAudienceScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.AudienceValueGenerator").getId());
        scopeBean.addScopeVariable(tenantAudienceScopeVariable);

        //Tenant app - site
        GlobalScopeVariableContext tenantSiteScopeVariable = new GlobalScopeVariableContext();
        tenantSiteScopeVariable.setLinkName("default_tenant_site");
        tenantSiteScopeVariable.setApplicableModuleId(modBean.getModule(FacilioConstants.ContextNames.SITE).getModuleId());
        tenantSiteScopeVariable.setStatus(true);
        tenantSiteScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));
        tenantSiteScopeVariable.setDescription("Default Site Scope Variable");
        tenantSiteScopeVariable.setDisplayName("Site Scope Variable");
        tenantSiteScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
        tenantSiteScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.SiteValueGenerator").getId());
        scopeBean.addScopeVariable(tenantSiteScopeVariable);
    }
}
