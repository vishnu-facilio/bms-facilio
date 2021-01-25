package com.facilio.bmsconsoleV3.signup.scoping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.Collections;

public class PeopleAnnouncementScopingConfig extends SignUpData {

    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS);

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
