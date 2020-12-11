package com.facilio.bmsconsoleV3.scoping;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.context.ModuleScopingConfiguration;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkorderScopingConfig extends ModuleScopingConfiguration {
    @Override
    public void addScopingConfiguration() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);

            //adding site scope in Facilio
            long applicationScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            scoping.setFieldName("siteId");
            scoping.setScopingId(applicationScopingId);
            scoping.setOperatorId(36);
            scoping.setFieldValueGenerator("com.facilio.modules.SiteValueGenerator");
            scoping.setModuleId(module.getModuleId());

            //adding wo scope in Occupant Portal
            long occupantScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
            ScopingConfigContext occupantScoping = new ScopingConfigContext();
            occupantScoping.setFieldName("requester");
            occupantScoping.setScopingId(occupantScopingId);
            occupantScoping.setOperatorId(36);
            occupantScoping.setFieldValueGenerator("com.facilio.modules.UserValueGenerator");
            occupantScoping.setModuleId(module.getModuleId());


            ApplicationApi.addScopingConfigForApp(Collections.singletonList(scoping));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
