package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;

public class UpdateSummaryWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SummaryWidget summaryWidget = (SummaryWidget) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        ApplicationContext app = appId != null ? ApplicationApi.getApplicationForId(appId) : null;

        if (summaryWidget != null) {

            if (app == null) {
                appId = summaryWidget.getAppId();
            }
            SummaryWidget existingSummaryWidget = SummaryWidgetUtil.getSummaryWidgetById(appId, summaryWidget.getId());
            FacilioUtil.throwIllegalArgumentException(existingSummaryWidget == null, "Summary widget does not exists");

            if (existingSummaryWidget.getDisplayName() == null || !existingSummaryWidget.getDisplayName().equals(summaryWidget.getDisplayName())) {
                SummaryWidgetUtil.updateSummaryWidget(summaryWidget);
            }

            context.put(FacilioConstants.SummaryWidget.SUMMARY_WIDGET_GROUPS, summaryWidget.getGroups());
            context.put(FacilioConstants.SummaryWidget.EXISTING_SUMMARY_WIDGET_GROUPS, existingSummaryWidget.getGroups());
        }

        context.put(FacilioConstants.ContextNames.ID, summaryWidget.getId());
        return false;
    }
}
