package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class UpdatePageWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PageSectionWidgetContext widget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET);
        if(widget != null) {
            CustomPageAPI.updatePageSectionWidgets(widget);

            context.put(FacilioConstants.CustomPage.WIDGET_DETAIL,
                    CustomPageAPI.parsePageWidgetDetails(widget.getWidgetType(), widget.getWidgetDetail()));
        }
        return false;
    }
}
