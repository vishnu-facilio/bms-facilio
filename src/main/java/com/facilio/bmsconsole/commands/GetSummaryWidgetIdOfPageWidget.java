package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;

public class GetSummaryWidgetIdOfPageWidget extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long pageWidgetId = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        if(pageWidgetId != null) {
            long summaryWidgetId = SummaryWidgetUtil.getSummaryWidgetIdForPageWidget(pageWidgetId);
            context.put(FacilioConstants.ContextNames.ID, summaryWidgetId);
        }
        return false;
    }
}
