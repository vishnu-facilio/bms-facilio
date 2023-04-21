package com.facilio.bmsconsole.widgetConfig;

import org.apache.commons.chain.Command;

public interface WidgetUpdateBuilder {
    WidgetCreateBuilder create();
    WidgetUpdateBuilder updateCommand(Command...updateCommand);
    WidgetSummaryBuilder summary(Command...summaryCommand);
    WidgetDeleteBuilder delete(Command...deleteCommand);
    WidgetConfig build();
}
