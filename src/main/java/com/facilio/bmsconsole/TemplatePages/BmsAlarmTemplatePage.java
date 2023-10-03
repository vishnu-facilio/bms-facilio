package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

import static com.facilio.connected.CommonConnectedSummaryAPI.getBMSAlarmSystemPage;

public class BmsAlarmTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.BMS_ALARM;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return getBMSAlarmSystemPage(app, false, true).get(0);
    }
}
