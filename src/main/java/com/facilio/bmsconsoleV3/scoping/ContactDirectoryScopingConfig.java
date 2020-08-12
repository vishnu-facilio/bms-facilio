package com.facilio.bmsconsoleV3.scoping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.ModuleScopingConfiguration;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.Collections;

public class ContactDirectoryScopingConfig extends ModuleScopingConfiguration {
    @Override
    public void addScopingConfiguration() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY);

            //adding building scope in Tenant Portal
            long applicationScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            scoping.setFieldName("contactdirectorysharing.contactDirectory");
            scoping.setScopingId(applicationScopingId);
            scoping.setOperatorId(88);
            scoping.setFieldValueGenerator("com.facilio.modules.CommunityBuildingValueGenerator");
            scoping.setModuleId(module.getModuleId());
            ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
