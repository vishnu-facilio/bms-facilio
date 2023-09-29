package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

import static com.facilio.connected.CommonConnectedSummaryAPI.getNewReadingRuleSystemPage;

public class ReadingRuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ReadingRules.NEW_READING_RULE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        return getNewReadingRuleSystemPage(app,false, true).get(0);
    }

}
