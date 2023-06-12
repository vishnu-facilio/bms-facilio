package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.modules.FacilioModule;

public interface TemplatePageFactory {
    String getModuleName();

    PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception;
}
