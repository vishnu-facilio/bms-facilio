package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;

public class GetSummaryWidgetIdOfWidget extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);

        if(widgetId != null) {
            long summaryWidgetId = SummaryWidgetUtil.getSummaryWidgetIdForWidgetId(widgetId, widgetWrapperType);
            context.put(FacilioConstants.ContextNames.ID, summaryWidgetId);
        }
        return false;
    }
}
