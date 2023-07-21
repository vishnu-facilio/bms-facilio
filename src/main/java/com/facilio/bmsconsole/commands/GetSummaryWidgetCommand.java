package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.context.SummaryWidgetGroup;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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

        if(summaryWidget != null) {
            //this is to fetch displayType of field in response of JSONObject as getAsJSON ignore displayType in FacilioField
            summaryWidget.getGroups().stream()
                    .map(SummaryWidgetGroup::getFields).collect(Collectors.toList())
                    .stream().flatMap(List::stream).forEach(f->{if(f.getField() != null){
                        f.setDisplayType(f.getField().getDisplayType());
                    }});
        }
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, summaryWidget);
        return false;
    }
}
