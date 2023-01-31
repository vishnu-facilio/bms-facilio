package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.constants.FacilioConstants;

public class ShiftPageFactory extends PageFactory {

    public static Page getShiftPage(Shift record) {

        Page page = new Page();
        page.addTab(composeSummaryTab(page));
        // page.addTab(composeRelatedTab(page)); // to be added post examining the use case
        page.addTab(composeHistoryTab(page));

        return page;
    }

    private static Page.Tab composeSummaryTab(Page page) {
        Page.Tab summaryTab = page.new Tab("Summary");
        summaryTab.addSection(composePrimarySection(page));
        return summaryTab;
    }

    private static Page.Section composePrimarySection(Page page) {
        Page.Section primarySection = page.new Section();

        // Shift Description
        int widgetWidth = 24;
        int widgetHeight = 3;
        int xOffset = 0;
        int yOffset = 0;
        primarySection.addWidget(composeWidget(PageWidget.WidgetType.SHIFT_DESCRIPTION, widgetHeight, widgetWidth, xOffset, yOffset));

        // Shift Start Time
        widgetWidth = 8;
        widgetHeight = 4;
        xOffset = 0;
        yOffset = 3;
        primarySection.addWidget(composeWidget(PageWidget.WidgetType.SHIFT_START_TIME, widgetHeight, widgetWidth, xOffset, yOffset));

        // Shift End Time
        widgetWidth = 8;
        widgetHeight = 4;
        xOffset = 8;
        yOffset = 3;
        primarySection.addWidget(composeWidget(PageWidget.WidgetType.SHIFT_END_TIME, widgetHeight, widgetWidth, xOffset, yOffset));

        // Shift Breaks
        widgetWidth = 8;
        widgetHeight = 4;
        xOffset = 0;
        yOffset = 7;
        primarySection.addWidget(composeWidget(PageWidget.WidgetType.SHIFT_BREAKS, widgetHeight, widgetWidth, xOffset, yOffset));

        // Shift Associated Employees
        widgetWidth = 8;
        widgetHeight = 4;
        xOffset = 8;
        yOffset = 7;
        primarySection.addWidget(composeWidget(PageWidget.WidgetType.SHIFT_ASSOCIATED_EMPLOYEES, widgetHeight, widgetWidth, xOffset, yOffset));

        // Shift Weekly Off
        widgetWidth = 8;
        widgetHeight = 8;
        xOffset = 16;
        yOffset = 3;
        primarySection.addWidget(composeWidget(PageWidget.WidgetType.SHIFT_WEEKLY_OFF, widgetHeight, widgetWidth, xOffset, yOffset));

        return primarySection;
    }

    private static PageWidget composeWidget(PageWidget.WidgetType widgetType, int height, int width, int xOffset, int yOffset){
        PageWidget widget = new PageWidget(widgetType);
        widget.addToLayoutParams(xOffset, yOffset, width, height);
        return widget;
    }

    private static Page.Tab composeRelatedTab(Page page) {
        Page.Tab relatedTab = page.new Tab("Related");
        Page.Section primarySection = page.new Section();
        PageWidget relatedWidget = new PageWidget(PageWidget.WidgetType.RELATED_RECORDS);
        relatedWidget.addToLayoutParams(primarySection, 24, 3);
        primarySection.addWidget(relatedWidget);
        relatedTab.addSection(primarySection);
        return relatedTab;
    }

    private static Page.Tab composeHistoryTab(Page page) {
        Page.Tab historyTab = page.new Tab("History");
        Page.Section primarySection = page.new Section();
        PageWidget historyWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        historyWidget.addToWidgetParams("activityModuleName", FacilioConstants.Shift.SHIFT_ACTIVITY);
        historyWidget.addToLayoutParams(primarySection, 24, 3);
        primarySection.addWidget(historyWidget);
        historyTab.addSection(primarySection);
        return historyTab;
    }
}
