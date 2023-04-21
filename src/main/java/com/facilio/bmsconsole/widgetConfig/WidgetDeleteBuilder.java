package com.facilio.bmsconsole.widgetConfig;

import org.apache.commons.chain.Command;

public interface WidgetDeleteBuilder {
    WidgetCreateBuilder create();
    WidgetUpdateBuilder update();
    WidgetDeleteBuilder deleteCommand(Command...deleteCommand);
    WidgetSummaryBuilder summary();
    WidgetConfig build();
}
