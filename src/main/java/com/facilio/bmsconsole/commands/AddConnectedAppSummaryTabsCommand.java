package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsoleV3.signup.util.PagesUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class AddConnectedAppSummaryTabsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long pageId = (long) context.get(FacilioConstants.CustomPage.PAGE_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String widgetName = (String) context.get(FacilioConstants.CustomPage.WIDGET_NAME);

        if(StringUtils.isNotBlank(widgetName)) {
            PageTabContext connectedTab = PagesUtil.addConnectedAppSummaryTabs(pageId, moduleName, widgetName);
            context.put(FacilioConstants.CustomPage.PAGE_TABS, Arrays.asList(connectedTab));
        } else {
            List<PageTabContext> tabs = PagesUtil.addConnectedAppSummaryTabs(pageId, moduleName);
            context.put(FacilioConstants.CustomPage.PAGE_TABS, tabs);
        }
        return false;
    }
}
