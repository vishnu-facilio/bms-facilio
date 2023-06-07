package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.bmsconsole.context.WidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddCommonModuleWidgetsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<WidgetContext> commonWidgets = getCommonModuleWidgets();
        if (CollectionUtils.isNotEmpty(commonWidgets)) {
            WidgetAPI.addWidgets(null,commonWidgets);
        }
        return false;
    }

    public List<WidgetContext> getCommonModuleWidgets() {
        List<WidgetContext> commonWidgets = new ArrayList<>();

        WidgetConfigContext w1Con1 = new WidgetConfigContext(WidgetConfigContext.ConfigType.FLEXIBLE, 4,12);
        WidgetContext wid1 = new WidgetContext("summaryfieldswidget", "Summary Fields Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, Arrays.asList(w1Con1));
        commonWidgets.add(wid1);

        WidgetConfigContext wid2Con1 = new WidgetConfigContext(WidgetConfigContext.ConfigType.FLEXIBLE, 4,12);
        WidgetContext wid2 = new WidgetContext("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, Arrays.asList(wid2Con1));
        commonWidgets.add(wid2);

        WidgetConfigContext wid3Con1 = new WidgetConfigContext(WidgetConfigContext.ConfigType.FLEXIBLE, 4,12);
        WidgetContext wid3 = new WidgetContext("comment", "Comment", PageWidget.WidgetType.COMMENT, Arrays.asList(wid3Con1));
        commonWidgets.add(wid3);

        WidgetConfigContext wid4Con1 = new WidgetConfigContext(WidgetConfigContext.ConfigType.FLEXIBLE, 4,12);
        WidgetContext wid4 = new WidgetContext("attachment", "Attachment", PageWidget.WidgetType.ATTACHMENT, Arrays.asList(wid4Con1));
        commonWidgets.add(wid4);

        WidgetConfigContext wid5Con1 = new WidgetConfigContext(WidgetConfigContext.ConfigType.FLEXIBLE, 4,12);
        WidgetContext wid5 = new WidgetContext("classification", "Classfication", PageWidget.WidgetType.CLASSIFICATION, Arrays.asList(wid5Con1));
        commonWidgets.add(wid5);

        WidgetConfigContext wid6Con1 = new WidgetConfigContext(WidgetConfigContext.ConfigType.FLEXIBLE, 2,12);
        WidgetContext wid6 = new WidgetContext("activity", "Activity", PageWidget.WidgetType.ACTIVITY, Arrays.asList(wid6Con1));
        commonWidgets.add(wid6);

        WidgetConfigContext wid7Con1 = new WidgetConfigContext(WidgetConfigContext.ConfigType.FLEXIBLE, 4,12);
        WidgetContext wid7 = new WidgetContext("failurereport", "Failure Report", PageWidget.WidgetType.FAILURE_REPORT, Arrays.asList(wid7Con1));
        commonWidgets.add(wid7);

        WidgetConfigContext wid8Con1 = new WidgetConfigContext(WidgetConfigContext.ConfigType.FLEXIBLE, 4,12);
        WidgetContext wid8 = new WidgetContext("bulkrelatedlist", "Bulk Related List", PageWidget.WidgetType.BULK_RELATED_LIST, Arrays.asList(wid8Con1));
        commonWidgets.add(wid8);

        WidgetConfigContext wid9Con1 = new WidgetConfigContext(WidgetConfigContext.ConfigType.FLEXIBLE, 4,12);
        WidgetContext wid9 = new WidgetContext("bulkrelationshipwidget", "Bulk Relationship Widget", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, Arrays.asList(wid9Con1));
        commonWidgets.add(wid9);

        return commonWidgets;
    }
}


