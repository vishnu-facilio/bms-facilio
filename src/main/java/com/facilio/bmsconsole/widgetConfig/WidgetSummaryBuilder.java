package com.facilio.bmsconsole.widgetConfig;

import org.apache.commons.chain.Command;

public interface WidgetSummaryBuilder {
    WidgetCreateBuilder create();
    WidgetUpdateBuilder update();
    WidgetSummaryBuilder fetchCommand(Command...fetchCommand);
    WidgetDeleteBuilder delete();
    WidgetConfig build();
}
