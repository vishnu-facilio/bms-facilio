package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

import static com.facilio.connected.CommonConnectedSummaryAPI.getReadingAlarmSystemPage;

public class ReadingAlarmTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.NEW_READING_ALARM;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return getReadingAlarmSystemPage(app, false, true).get(0);
    }

}
