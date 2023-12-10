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
        maintenanceSiteScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.AccessibleBasespaceValueGenerator").getId());
        scopeBean.addScopeVariable(maintenanceSiteScopeVariable);

        //Occupant app - user
//        GlobalScopeVariableContext occupantUserScopeVariable = new GlobalScopeVariableContext();
//        occupantUserScopeVariable.setLinkName("default_occupant_user");
//        occupantUserScopeVariable.setApplicableModuleName(FacilioConstants.ContextNames.USERS);
//        occupantUserScopeVariable.setStatus(true);
//        occupantUserScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP));
//        occupantUserScopeVariable.setDescription("Default User Scope Variable");
//        occupantUserScopeVariable.setDisplayName("User Scope Variable");
//        occupantUserScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
//        occupantUserScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.CurrentUserValueGenerator").getId());
//        scopeBean.addScopeVariable(occupantUserScopeVariable);

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

        //Client app - client
        GlobalScopeVariableContext clientUserScopeVariable = new GlobalScopeVariableContext();
        clientUserScopeVariable.setLinkName("default_client_user");
        clientUserScopeVariable.setApplicableModuleId(modBean.getModule(FacilioConstants.ContextNames.CLIENT).getModuleId());
        clientUserScopeVariable.setStatus(true);
        clientUserScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP));
        clientUserScopeVariable.setDescription("Default User Scope Variable");
        clientUserScopeVariable.setDisplayName("User Scope Variable");
        clientUserScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
        clientUserScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.ClientValueGenerator").getId());
        scopeBean.addScopeVariable(clientUserScopeVariable);

        //Remote Monitoring - client
        GlobalScopeVariableContext clientScopeVariable = new GlobalScopeVariableContext();
        clientScopeVariable.setLinkName("default_remotemonitor_client");
        clientScopeVariable.setApplicableModuleId(modBean.getModule(FacilioConstants.ContextNames.CLIENT).getModuleId());
        clientScopeVariable.setStatus(true);
        clientScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING));
        clientScopeVariable.setDescription("Default Client Scope Variable");
        clientScopeVariable.setDisplayName("Client Scope Variable");
        clientScopeVariable.setType(GlobalScopeVariableContext.Type.ALL.getIndex());
        clientScopeVariable.setShowSwitch(true);
        scopeBean.addScopeVariable(clientScopeVariable);

        //energy app - Site
        GlobalScopeVariableContext energySiteScopeVariable = new GlobalScopeVariableContext();
        energySiteScopeVariable.setLinkName("default_energy_site");
        energySiteScopeVariable.setApplicableModuleId(modBean.getModule(FacilioConstants.ContextNames.SITE).getModuleId());
        energySiteScopeVariable.setStatus(true);
        energySiteScopeVariable.setShowSwitch(true);
        energySiteScopeVariable.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        energySiteScopeVariable.setDescription("Default Site Scope Variable");
        energySiteScopeVariable.setDisplayName("Site Scope Variable");
        energySiteScopeVariable.setType(GlobalScopeVariableContext.Type.SCOPED.getIndex());
        energySiteScopeVariable.setValueGeneratorId(valGenBean.getValueGenerator("com.facilio.modules.AccessibleBasespaceValueGenerator").getId());
        scopeBean.addScopeVariable(energySiteScopeVariable);

        //if you default GlobalScopeVariable Please add linkName in PackageConstants.GlobalScopeVariableConstants.DEFAULT_CREATED_GLOBAL_SCOPE_VARIABLE also
    }
}
