package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import java.util.Objects;

public class AddPageSummaryWidgetCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(AddCustomPageCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long pageWidgetId = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        Long summaryWidgetId = (Long) context.get(FacilioConstants.ContextNames.ID);

        if(pageWidgetId == null) {
            LOGGER.warn("Page section widget id can't be null");
        } else if (summaryWidgetId == null) {
            LOGGER.warn("Summary widget id can't be null");
        }
        else {
            Long existingSummaryWidgetId = SummaryWidgetUtil.getSummaryWidgetIdForPageWidget(pageWidgetId);

            if (existingSummaryWidgetId == -1L) {
                SummaryWidgetUtil.addPageSummaryWidget(pageWidgetId, summaryWidgetId);
            } else if (!existingSummaryWidgetId.equals(summaryWidgetId)) {
                SummaryWidgetUtil.updatePageSummaryWidget(pageWidgetId, summaryWidgetId);
            }

            context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, SummaryWidgetUtil.getSummaryWidgetById(-1, summaryWidgetId));
        }
        return false;
    }
}
