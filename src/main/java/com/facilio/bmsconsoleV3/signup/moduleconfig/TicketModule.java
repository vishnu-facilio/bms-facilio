package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TicketModule extends BaseModuleConfig{

    public TicketModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.TICKET);
    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields maintenanceApp = new ScopeVariableModulesFields();
        maintenanceApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_maintenance_site"));
        maintenanceApp.setModuleId(module.getModuleId());
        maintenanceApp.setFieldName("siteId");

        ScopeVariableModulesFields tenantApp = new ScopeVariableModulesFields();
        tenantApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_tenant_user"));
        tenantApp.setModuleId(module.getModuleId());
        tenantApp.setFieldName("tenant");

        ScopeVariableModulesFields vendorApp = new ScopeVariableModulesFields();
        vendorApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_vendor_user"));
        vendorApp.setModuleId(module.getModuleId());
        vendorApp.setFieldName("vendor");

        scopeConfigList = Arrays.asList(maintenanceApp,tenantApp,vendorApp);
        return scopeConfigList;
    }
}
