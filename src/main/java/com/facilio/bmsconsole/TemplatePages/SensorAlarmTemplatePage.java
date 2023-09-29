package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

import static com.facilio.bmsconsole.util.NewAlarmAPI.getSensorAlarmSystemPage;

public class SensorAlarmTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return getSensorAlarmSystemPage(app, false, true).get(0);
    }
}