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
                        .addWidgetConfig("flexiblewebsummaryfieldswidget_24","Summary Fields Widget - 24", WidgetConfigContext.ConfigType.FLEXIBLE,24, -1, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobilesummaryfieldswidget_8","Summary Fields Widget - 8", 8, PagesContext.PageLayoutType.MOBILE)
                        .addWidgetConfig("flexiblewebsummaryfieldswidget_26","Summary Fields Widget - 26",WidgetConfigContext.ConfigType.FLEXIBLE,26,-1,PagesContext.PageLayoutType.WEB)
                        .addWidgetConfig("flexiblewebsummaryfieldswidget_20","Summary Fields Widget - 20",WidgetConfigContext.ConfigType.FLEXIBLE,20,-1,PagesContext.PageLayoutType.WEB)
                    .done()
                    .addModuleWidget("widgetgroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP)
                        .addFlexibleWidgetConfig("flexiblewebwidgetgroup_20","Widget Group - 20", 20, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobilewidgetgroup_8","Widget Group - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("comment", "Comment", PageWidget.WidgetType.COMMENT)
                        .addFixedWidgetConfig("fixedwebwidget_28*6","Comment - 28*6",28,6,PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblewebcomment_27","Comment - 27", 27, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobilecomment_8","Comment - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("attachment", "Attachment", PageWidget.WidgetType.ATTACHMENT)
                        .addFlexibleWidgetConfig("flexiblewebattachment_27","Attachment - 27",27, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobileattachment_8","Attachment - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("classification", "Classfication", PageWidget.WidgetType.CLASSIFICATION)
                        .addFlexibleWidgetConfig("flexiblewebclassification_28","Classfication - 28", 28, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobileclassification_8","Classfication - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("activity", "Activity", PageWidget.WidgetType.ACTIVITY)
                        .addFlexibleWidgetConfig("flexiblewebactivity_20","Activity - 20",20, PagesContext.PageLayoutType.WEB)
                    .done()
                    .addModuleWidget("failurereport", "Failure Report", PageWidget.WidgetType.FAILURE_REPORT)
                        .addFlexibleWidgetConfig("flexiblewebfailurereport_29","Failure Report - 29", 29, PagesContext.PageLayoutType.WEB)
                    .done()
                    .addModuleWidget("bulkrelatedlist", "Bulk Related List", PageWidget.WidgetType.BULK_RELATED_LIST)
                        .addFlexibleWidgetConfig("flexiblewebbulkrelatedlist_29","Bulk Related List - 29",29, PagesContext.PageLayoutType.WEB)
                        .addFixedWidgetConfig("fixedrelatedListwidgetViewWidget-28*12", "Full Width Control Action Command View Widget - 28*12",28,12, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobilebulkrelatedlist_8","Bulk Related List - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("bulkrelationshipwidget", "Bulk Relationship Widget", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET)
                        .addFlexibleWidgetConfig("flexiblewebbulkrelationshipwidget_29","Bulk Relationship Widget - 29",29, PagesContext.PageLayoutType.WEB)
                        .addFlexibleWidgetConfig("flexiblemobilebulkrelationshipwidget_8","Bulk Relationship Widget - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("statetransitiontimelog", "STATE TRANSITION TIME LOG", PageWidget.WidgetType.STATE_TRANSITION_TIME_LOG)
                        .addFlexibleWidgetConfig("flexiblewebstatetransitiontimelog_30","Time Log - 30", 30, PagesContext.PageLayoutType.WEB)
                    .done()
                     .addModuleWidget("relatedlistwidget", "Related List", PageWidget.WidgetType.RELATED_LIST)
                        .addFixedWidgetConfig("relatedlistwidgetViewWidget-65*12", "Related List View Widget - 65*12",65,12, PagesContext.PageLayoutType.WEB)
                        .addFixedWidgetConfig("relatedListwidgetViewWidget-28*12", "Full Width Control Action Command View Widget - 28*12",28,12, PagesContext.PageLayoutType.WEB)
                     .done()
                .getWidgets();
    }
}


