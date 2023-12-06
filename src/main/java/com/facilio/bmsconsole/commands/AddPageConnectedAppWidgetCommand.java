package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PageConnectedAppWidgetContext;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.PageConnectedAppWidgetUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

@Log4j
public class AddPageConnectedAppWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PageConnectedAppWidgetContext pageConnectedAppWidgetContext = (PageConnectedAppWidgetContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);

        if(widgetId == null || widgetId <= 0) {
            LOGGER.warn("widgetId should be defined");
        } else if (pageConnectedAppWidgetContext == null || pageConnectedAppWidgetContext.getConnectedAppWidgetId() <= 0) {
            LOGGER.warn("ConnectedApp widget id should be defined");
        } else {
            pageConnectedAppWidgetContext.setWidgetId(widgetId);
            long connectedAppWidgetId = pageConnectedAppWidgetContext.getConnectedAppWidgetId();
            Long existingConnectedAppWidgetId = PageConnectedAppWidgetUtil.getConnectedAppWidgetIdForWidgetId(widgetId);

            if (existingConnectedAppWidgetId == -1L) {
                PageConnectedAppWidgetUtil.addPageConnectedAppWidget(widgetId, connectedAppWidgetId);
            } else if (!existingConnectedAppWidgetId.equals(connectedAppWidgetId)) {
                PageConnectedAppWidgetUtil.updatePageConnectedAppWidget(widgetId, connectedAppWidgetId);
            }

            context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, PageConnectedAppWidgetUtil.getPageConnectedAppWidget(widgetId, WidgetWrapperType.DEFAULT));
        }
        return false;
    }
}
