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

public class SiteScopingConfig extends ModuleScopingConfiguration {
    @Override
    public void addScopingConfiguration() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);

            //adding site scope in Facilio
            long applicationScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
            ScopingConfigContext scoping = new ScopingConfigContext();
            scoping.setFieldName("siteId");
            scoping.setScopingId(applicationScopingId);
            scoping.setOperatorId(36);
            scoping.setFieldValueGenerator("com.facilio.modules.SiteValueGenerator");
            scoping.setModuleId(module.getModuleId());

            //adding site scope in tenant portal
            long tenantPortalScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
            ScopingConfigContext tenantScoping = new ScopingConfigContext();
            tenantScoping.setFieldName("siteId");
            tenantScoping.setScopingId(tenantPortalScopingId);
            tenantScoping.setOperatorId(36);
            tenantScoping.setFieldValueGenerator("com.facilio.modules.SiteValueGenerator");
            tenantScoping.setModuleId(module.getModuleId());

            //adding site scope in occupant portal
            long occupantPortalScopingId = ApplicationApi.addScoping(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
            ScopingConfigContext occupantScoping = new ScopingConfigContext();
            occupantScoping.setFieldName("siteId");
            occupantScoping.setScopingId(occupantPortalScopingId);
            occupantScoping.setOperatorId(36);
            occupantScoping.setFieldValueGenerator("com.facilio.modules.SiteValueGenerator");
            occupantScoping.setModuleId(module.getModuleId());

            List<ScopingConfigContext> scopingConfig = new ArrayList<>();
            scopingConfig.add(scoping);
            scopingConfig.add(tenantScoping);
            scopingConfig.add(occupantScoping);

            ApplicationApi.addScopingConfigForApp(scopingConfig);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
