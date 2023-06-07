package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;

import java.util.Objects;

public class AddPageSummaryWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long pageWidgetId = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        Long summaryWidgetId = (Long) context.get(FacilioConstants.ContextNames.ID);

        Objects.requireNonNull(pageWidgetId, "Page section widget id can't be null");
        Objects.requireNonNull(summaryWidgetId, "Summary widget id can't be null");
        Long existingSummaryWidgetId = SummaryWidgetUtil.getSummaryWidgetIdForPageWidget(pageWidgetId);

        if(existingSummaryWidgetId == -1L) {
            SummaryWidgetUtil.addPageSummaryWidget(pageWidgetId, summaryWidgetId);
        }
        else if (!existingSummaryWidgetId.equals(summaryWidgetId)) {
            SummaryWidgetUtil.updatePageSummaryWidget(pageWidgetId, summaryWidgetId);
        }
        return false;
    }
}
