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

public class WorkPermitScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule workPermitModule = modBean.getModule(FacilioConstants.ContextNames.WorkPermit.WORKPERMIT);

            //adding vendor scope in vendor portal
            long vendorPortalScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            scoping.setFieldName("vendor");
            scoping.setScopingId(vendorPortalScopingId);
            scoping.setOperatorId(36);
            scoping.setFieldValueGenerator("com.facilio.modules.UserValueGenerator");
            scoping.setModuleId(workPermitModule.getModuleId());

            //adding tenant scope in tenant portal
            long tenantPortalScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext tenantScoping = new ScopingConfigContext();
            tenantScoping.setFieldName("tenant");
            tenantScoping.setScopingId(tenantPortalScopingId);
            tenantScoping.setOperatorId(36);
            tenantScoping.setFieldValueGenerator("com.facilio.modules.UserValueGenerator");
            tenantScoping.setModuleId(workPermitModule.getModuleId());

            //adding site scope in Facilio
            long applicationScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            ScopingConfigContext appScoping = new ScopingConfigContext();
            appScoping.setFieldName("siteId");
            appScoping.setScopingId(applicationScopingId);
            appScoping.setOperatorId(36);
            appScoping.setFieldValueGenerator("com.facilio.modules.SiteValueGenerator");
            appScoping.setModuleId(workPermitModule.getModuleId());

            List<ScopingConfigContext> scopingConfig = new ArrayList<>();
            scopingConfig.add(scoping);
            scopingConfig.add(tenantScoping);
            scopingConfig.add(appScoping);

            ApplicationApi.addScopingConfigForApp(scopingConfig);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
