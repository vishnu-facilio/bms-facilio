package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsoleV3.signup.moduleconfig.FlaggedEventModuleConfig;
import com.facilio.modules.FacilioModule;
import com.facilio.remotemonitoring.signup.FlaggedEventModule;

import java.util.Collections;
import java.util.List;

public class FlaggedAlarmTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FlaggedEventModule.MODULE_NAME;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        List<PagesContext> pagesForCurrentApp =  FlaggedEventModuleConfig.getSystemPagesMap(getModuleName()).get(app.getLinkName());
        if(!pagesForCurrentApp.isEmpty()){
            return pagesForCurrentApp.get(0);
        }
        return null;
    }
}
