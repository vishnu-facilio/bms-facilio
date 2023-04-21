package com.facilio.bmsconsole.widgetConfig;

public interface WidgetBuilder {
    WidgetCreateBuilder create();
    WidgetUpdateBuilder update();
    WidgetDeleteBuilder delete();
    WidgetSummaryBuilder summary();
    WidgetConfig build();

}
