package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.RelatedListWidgetContext;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetRelatedListWidgetDetailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        if((widgetId == null || widgetId <= 0) ) {
            throw new IllegalArgumentException("widgetId should be defined, to get related list widget detail");
        }
        RelatedListWidgetContext relatedList = RelatedListWidgetUtil.getRelatedListOfWidgetId(widgetId, widgetWrapperType);
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, relatedList);
        return false;
    }
}
