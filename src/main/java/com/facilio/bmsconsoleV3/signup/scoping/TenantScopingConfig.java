package com.facilio.bmsconsoleV3.signup.scoping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.ArrayList;
import java.util.List;

public class TenantScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);

            //adding site scope in Facilio
            long applicationScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            scoping.setFieldName("siteId");
            scoping.setScopingId(applicationScopingId);
            scoping.setOperatorId(36);
            scoping.setFieldValueGenerator("com.facilio.modules.SiteValueGenerator");
            scoping.setModuleId(module.getModuleId());

            //adding tenant scope in tenant portal
            long tenantPortalScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext tenantScoping = new ScopingConfigContext();
            tenantScoping.setFieldName("siteId");
            tenantScoping.setScopingId(tenantPortalScopingId);
            tenantScoping.setOperatorId(36);
            tenantScoping.setFieldValueGenerator("com.facilio.modules.SiteValueGenerator");
            tenantScoping.setModuleId(module.getModuleId());

            List<ScopingConfigContext> scopingConfig = new ArrayList<>();
            scopingConfig.add(scoping);
            scopingConfig.add(tenantScoping);

            ApplicationApi.addScopingConfigForApp(scopingConfig);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
