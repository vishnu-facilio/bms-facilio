package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BulkRelatedListContext;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class GetBulkRelatedListWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        if((widgetId == null || widgetId <= 0)) {
            throw new IllegalArgumentException("Invalid widgetId to fetch related list");
        }

        BulkRelatedListContext bulkRelList = RelatedListWidgetUtil.getBulkRelatedListOfWidgetId(widgetId, widgetWrapperType);
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, bulkRelList);
        return false;
    }
}
