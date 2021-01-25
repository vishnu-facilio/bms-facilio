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

public class VendorScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule vendorsModule = modBean.getModule(FacilioConstants.ContextNames.VENDORS);

            //adding tenant scope in tenant portal
            long tenantPortalScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext tenantScoping = new ScopingConfigContext();
            tenantScoping.setFieldName("tenant");
            tenantScoping.setScopingId(tenantPortalScopingId);
            tenantScoping.setOperatorId(36);
            tenantScoping.setFieldValueGenerator("com.facilio.modules.UserValueGenerator");
            tenantScoping.setModuleId(vendorsModule.getModuleId());

            List<ScopingConfigContext> scopingConfig = new ArrayList<>();
            scopingConfig.add(tenantScoping);

            ApplicationApi.addScopingConfigForApp(scopingConfig);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
