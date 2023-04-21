package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Objects;

public class GetPageSectionWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        PageSectionWidgetContext widget = (PageSectionWidgetContext) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET);

        if(widget == null) {
            FacilioUtil.throwIllegalArgumentException(id <= 0, "Invalid id to get widget");

            widget = CustomPageAPI.getPageSectionWidget(id);

            Objects.requireNonNull(widget, "Widget does not exists");

            context.put(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET, widget);
        }
        return false;
    }
}
