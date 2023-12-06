package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PageConnectedAppWidgetContext;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.util.PageConnectedAppWidgetUtil;
import org.apache.commons.chain.Context;

public class GetPageConnectedAppWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);

        PageConnectedAppWidgetContext pageConnectedAppWidgetContext = PageConnectedAppWidgetUtil.getPageConnectedAppWidget(widgetId, WidgetWrapperType.DEFAULT);

        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, pageConnectedAppWidgetContext);
        return false;
    }
}
