package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.signup.ServicePMTemplateModule;
import com.facilio.modules.FacilioModule;

import java.util.List;

public class ServicePMTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE;
    }
    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        List<PagesContext> templatePages =  ServicePMTemplateModule.getSystemPage(app, module, true, false);
        return templatePages.get(0);
    }
}
