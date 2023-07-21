package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

public class AddPageSummaryWidgetCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(AddCustomPageCommand.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        Long summaryWidgetId = (Long) context.get(FacilioConstants.ContextNames.ID);

        if(widgetId == null || widgetId <= 0) {
            LOGGER.warn("widgetId should be defined");
        } else if (summaryWidgetId == null) {
            LOGGER.warn("Summary widget id can't be null");
        } else {
            Long existingSummaryWidgetId = SummaryWidgetUtil.getSummaryWidgetIdForWidgetId(widgetId, widgetWrapperType);

            if (existingSummaryWidgetId == -1L) {
                SummaryWidgetUtil.addPageSummaryWidget(widgetId, widgetWrapperType, summaryWidgetId);
            } else if (!existingSummaryWidgetId.equals(summaryWidgetId)) {
                SummaryWidgetUtil.updatePageSummaryWidget(widgetId, widgetWrapperType, summaryWidgetId);
            }

            context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, SummaryWidgetUtil.getSummaryWidgetById(-1, summaryWidgetId));
        }
        return false;
    }
}
