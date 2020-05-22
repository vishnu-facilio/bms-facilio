package com.facilio.bmsconsoleV3.scoping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.ModuleScopingConfiguration;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.Collections;

public class InsuranceScopingConfig extends ModuleScopingConfiguration {
    @Override
    public void addScopingConfiguration() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule insuranceModule = modBean.getModule(FacilioConstants.ContextNames.INSURANCE);

            //adding vendor scope in vendor portal
            long vendorPortalScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            scoping.setFieldName("vendor");
            scoping.setScopingId(vendorPortalScopingId);
            scoping.setOperatorId(36);
            scoping.setFieldValueGenerator("com.facilio.modules.UserValueGenerator");
            scoping.setModuleId(insuranceModule.getModuleId());
            ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
