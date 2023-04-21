package com.facilio.bmsconsole.widgetConfig;

import com.facilio.qa.rules.Constants;
import org.apache.commons.chain.Command;

public interface WidgetCreateBuilder {
    WidgetCreateBuilder saveCommand(Command...saveCommand);
    WidgetUpdateBuilder update(Command...updateCommand);
    WidgetSummaryBuilder summary(Command...summaryCommand);
    WidgetDeleteBuilder delete(Command...deleteCommand);
    WidgetConfig build();

}
