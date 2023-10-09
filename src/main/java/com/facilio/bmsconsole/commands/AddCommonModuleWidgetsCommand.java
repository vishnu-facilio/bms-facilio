package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ModuleWidgets;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.bmsconsole.context.WidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

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

         return new ModuleWidgets()
                    .addModuleWidget("summaryfieldswidget", "Summary Fields Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET)
                        .addWidgetConfig("flexiblewebsummaryfieldswidget_5","Summary Fields Widget - 5", WidgetConfigContext.ConfigType.FLEXIBLE,5, -1, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobilesummaryfieldswidget_8","Summary Fields Widget - 8", 8, PagesContext.PageLayoutType.MOBILE)
                        .addWidgetConfig("flexiblewebsummaryfieldswidget_4","Summary Fields Widget - 4",WidgetConfigContext.ConfigType.FLEXIBLE,4,-1,PagesContext.PageLayoutType.WEB)
                        .addWidgetConfig("flexiblewebsummaryfieldswidget_7","Summary Fields Widget - 7",WidgetConfigContext.ConfigType.FLEXIBLE,7,-1,PagesContext.PageLayoutType.WEB)
                 .addWidgetConfig("flexiblewebsummaryfieldswidget_6_9","Summary Fields Widget - 6 -9",WidgetConfigContext.ConfigType.FIXED,6,9,PagesContext.PageLayoutType.WEB)
                    .done()
                    .addModuleWidget("widgetgroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP)
                        .addFlexibleWidgetConfig("flexiblewebwidgetgroup_4","Widget Group - 4", 4, PagesContext.PageLayoutType.WEB)
                        .addFixedWidgetConfig("webwidgetgroup_6_6","Widget Group - 6 - 6", 6,6, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobilewidgetgroup_8","Widget Group - 8", 8, PagesContext.PageLayoutType.MOBILE)
                        .addFlexibleWidgetConfig("flexiblewebwidgetgroup_10","Widget Group - 10", 10, PagesContext.PageLayoutType.WEB)
                    .done()
                    .addModuleWidget("comment", "Comment", PageWidget.WidgetType.COMMENT)
                        .addFlexibleWidgetConfig("flexiblewebcomment_5","Comment - 5", 5, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobilecomment_8","Comment - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("attachment", "Attachment", PageWidget.WidgetType.ATTACHMENT)
                        .addFlexibleWidgetConfig("flexiblewebattachment_5","Attachment - 5",5, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobileattachment_8","Attachment - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("classification", "Classfication", PageWidget.WidgetType.CLASSIFICATION)
                        .addFlexibleWidgetConfig("flexiblewebclassification_6","Classfication - 6", 6, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobileclassification_8","Classfication - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("activity", "Activity", PageWidget.WidgetType.ACTIVITY)
                 .addFlexibleWidgetConfig("flexiblewebactivity_4","Activity - 4",4, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfig("flexiblewebactivity_10","Activity - 10",WidgetConfigContext.ConfigType.FLEXIBLE,10,-1,PagesContext.PageLayoutType.WEB)
                    .done()
                    .addModuleWidget("failurereport", "Failure Report", PageWidget.WidgetType.FAILURE_REPORT)
                        .addFlexibleWidgetConfig("flexiblewebfailurereport_6","Failure Report - 6", 6, PagesContext.PageLayoutType.WEB)
                    .done()
                    .addModuleWidget("bulkrelatedlist", "Bulk Related List", PageWidget.WidgetType.BULK_RELATED_LIST)
                        .addFlexibleWidgetConfig("flexiblewebbulkrelatedlist_6","Bulk Related List - 6",6, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobilebulkrelatedlist_8","Bulk Related List - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("bulkrelationshipwidget", "Bulk Relationship Widget", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET)
                        .addFlexibleWidgetConfig("flexiblewebbulkrelationshipwidget_6","Bulk Relationship Widget - 6",6, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobilebulkrelationshipwidget_8","Bulk Relationship Widget - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("statetransitiontimelog", "State Transition Time Log", PageWidget.WidgetType.STATE_TRANSITION_TIME_LOG)
                        .addFlexibleWidgetConfig("flexiblewebstatetransitiontimelog_6","Time Log - 6", 6, PagesContext.PageLayoutType.WEB)
                    .done()
                     .addModuleWidget("relatedlistwidget", "Related List", PageWidget.WidgetType.RELATED_LIST)
                        .addFixedWidgetConfig("relatedListwidgetViewWidget_6_12", "Full Width Control Action Command View Widget - 6 - 12",6,12, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblewebrelatedlist_6","Related List - 6",6, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobilerelatedlist_8","Related List - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("connectedApp", "Connected App", PageWidget.WidgetType.CONNNECTED_APP)
                        .addWidgetConfig("flexiblewebconnectedapp_6","Connected App - 6", WidgetConfigContext.ConfigType.FLEXIBLE, 6, -1, PagesContext.PageLayoutType.WEB)
                    .done()
                .getWidgets();
    }
}


