package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class GetSummaryWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long appId = (Long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1L);
        appId = appId == null ? -1L:appId;

        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        String name = (String) context.get(FacilioConstants.SummaryWidget.SUMMARY_WIDGET_NAME);

        SummaryWidget summaryWidget = null;
        if(id !=null && id > 0) {
            summaryWidget = SummaryWidgetUtil.getSummaryWidgetById(appId, id);
        }
        else if(StringUtils.isNotEmpty(name)){
            summaryWidget = SummaryWidgetUtil.getSummaryWidgetByName(appId, name);
        }
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, summaryWidget);
        return false;
    }
}
