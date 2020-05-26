package com.facilio.bmsconsoleV3.scoping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.ModuleScopingConfiguration;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.ArrayList;
import java.util.List;

public class VisitorLoggingScopingConfig extends ModuleScopingConfiguration {
    @Override
    public void addScopingConfiguration() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule visitorLoggingModule = modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING);

            //adding vendor scope in vendor portal
            long vendorPortalScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            scoping.setFieldName("vendor");
            scoping.setScopingId(vendorPortalScopingId);
            scoping.setOperatorId(36);
            scoping.setFieldValueGenerator("com.facilio.modules.UserValueGenerator");
            scoping.setModuleId(visitorLoggingModule.getModuleId());

            //adding tenant scope in tenant portal
            long tenantPortalScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext tenantScoping = new ScopingConfigContext();
            tenantScoping.setFieldName("tenant");
            tenantScoping.setScopingId(tenantPortalScopingId);
            tenantScoping.setOperatorId(36);
            tenantScoping.setFieldValueGenerator("com.facilio.modules.UserValueGenerator");
            tenantScoping.setModuleId(visitorLoggingModule.getModuleId());

            List<ScopingConfigContext> scopingConfig = new ArrayList<>();
            scopingConfig.add(scoping);
            scopingConfig.add(tenantScoping);

            ApplicationApi.addScopingConfigForApp(scopingConfig);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
