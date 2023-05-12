package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WidgetGroupConfigContext;
import com.facilio.bmsconsole.context.WidgetGroupContext;
import com.facilio.bmsconsole.util.WidgetGroupUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class GetWidgetGroupConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        if(widgetId != null){
            WidgetGroupContext widgetGroup = new WidgetGroupContext();
            WidgetGroupConfigContext config = WidgetGroupUtil.getWidgetGroupConfig(widgetId);
            FacilioUtil.throwIllegalArgumentException(config==null, "Invalid widget id to fetch widgetGroup");
            widgetGroup.setConfig(config);
            context.put(FacilioConstants.WidgetNames.WIDGET_GROUP, widgetGroup);
        }
        return false;
    }
}
