package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.RelationshipWidget;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.apache.commons.chain.Context;

public class GetRelationshipWidgetDetailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        if((widgetId == null || widgetId <= 0) ) {
            throw new IllegalArgumentException("widgetId should be defined, to get relationship widget detail");
        }
        RelationshipWidget relationship = RelationshipWidgetUtil.getRelationshipOfWidget(widgetId, widgetWrapperType);
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, relationship);
        return false;
    }
}
