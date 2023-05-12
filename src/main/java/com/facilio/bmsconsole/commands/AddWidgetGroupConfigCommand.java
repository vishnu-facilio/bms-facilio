package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WidgetGroupConfigContext;
import com.facilio.bmsconsole.context.WidgetGroupContext;
import com.facilio.bmsconsole.util.WidgetGroupUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Objects;


public class AddWidgetGroupConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        Objects.requireNonNull(widgetId, "widgetId can't be null");

        WidgetGroupContext widgetGroup  = (WidgetGroupContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
        if(widgetGroup!= null) {
            WidgetGroupConfigContext config = widgetGroup.getConfig();
            config.setWidgetId(widgetId);

            WidgetGroupUtil.insertWidgetGroupConfigToDB(config);

            if(CollectionUtils.isNotEmpty(widgetGroup.getSections())) {
                context.put(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTIONS, widgetGroup.getSections());
            }
        }

        return false;
    }
}
