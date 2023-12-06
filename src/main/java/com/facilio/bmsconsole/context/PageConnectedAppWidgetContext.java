package com.facilio.bmsconsole.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageConnectedAppWidgetContext extends PageSectionWidgetContext {
    public PageConnectedAppWidgetContext() {
    }
    public PageConnectedAppWidgetContext(long widgetId, long connectedAppWidgetId) {
        this.widgetId = widgetId;
        this.connectedAppWidgetId = connectedAppWidgetId;
    }
    private long widgetId;
    private long connectedAppWidgetId;
}
