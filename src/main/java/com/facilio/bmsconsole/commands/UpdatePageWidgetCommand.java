package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class UpdatePageWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        PageSectionWidgetContext widget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET);
        if(widget != null && widgetWrapperType != null) {
            CustomPageAPI.updatePageWidgets(widget, widgetWrapperType);

            context.put(FacilioConstants.CustomPage.WIDGET_DETAIL,
                    WidgetAPI.parsePageWidgetDetails(widget.getWidgetType(), widget.getWidgetDetail()));
        }
        context.put(FacilioConstants.CustomPage.WIDGETID, widget.getId());
        return false;
    }
}
