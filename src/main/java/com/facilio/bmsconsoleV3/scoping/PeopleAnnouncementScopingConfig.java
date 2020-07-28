package com.facilio.bmsconsoleV3.scoping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.ModuleScopingConfiguration;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.Collections;

public class PeopleAnnouncementScopingConfig extends ModuleScopingConfiguration {

    @Override
    public void addScopingConfiguration() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE_ANNOUNCEMENTS);

            //adding user scope in Tenant Portal
            long applicationScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            scoping.setFieldName("people");
            scoping.setScopingId(applicationScopingId);
            scoping.setOperatorId(36);
            scoping.setFieldValueGenerator("com.facilio.modules.PeopleValueGenerator");
            scoping.setModuleId(module.getModuleId());
            ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
