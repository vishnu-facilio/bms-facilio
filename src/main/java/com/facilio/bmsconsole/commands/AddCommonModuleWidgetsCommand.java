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

         return new ModuleWidgets()
                    .addModuleWidget("summaryfieldswidget", "Summary Fields Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET)
                        .addWidgetConfigs("flexiblewebsummaryfieldswidget_24","Summary Fields Widget - 24", 24, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilesummaryfieldswidget_8","Summary Fields Widget - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("widgetgroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP)
                        .addWidgetConfigs("flexiblewebwidgetgroup_20","Widget Group - 20", 20, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilewidgetgroup_8","Widget Group - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("comment", "Comment", PageWidget.WidgetType.COMMENT)
                        .addWidgetConfigs("flexiblewebcomment_27","Comment - 27", 27, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilecomment_8","Comment - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("attachment", "Attachment", PageWidget.WidgetType.ATTACHMENT)
                        .addWidgetConfigs("flexiblewebattachment_27","Attachment - 27",27, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobileattachment_8","Attachment - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("classification", "Classfication", PageWidget.WidgetType.CLASSIFICATION)
                        .addWidgetConfigs("flexiblewebclassification_28","Classfication - 28", 28, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobileclassification_8","Classfication - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("activity", "Activity", PageWidget.WidgetType.ACTIVITY)
                        .addWidgetConfigs("flexiblewebactivity_20","Activity - 20",20, PagesContext.PageLayoutType.WEB)
                    .done()
                    .addModuleWidget("failurereport", "Failure Report", PageWidget.WidgetType.FAILURE_REPORT)
                        .addWidgetConfigs("flexiblewebfailurereport_29","Failure Report - 29", 29, PagesContext.PageLayoutType.WEB)
                    .done()
                    .addModuleWidget("bulkrelatedlist", "Bulk Related List", PageWidget.WidgetType.BULK_RELATED_LIST)
                        .addWidgetConfigs("flexiblewebbulkrelatedlist_29","Bulk Related List - 29",29, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilebulkrelatedlist_8","Bulk Related List - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("bulkrelationshipwidget", "Bulk Relationship Widget", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET)
                        .addWidgetConfigs("flexiblewebbulkrelationshipwidget_29","Bulk Relationship Widget - 29",29, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilebulkrelationshipwidget_8","Bulk Relationship Widget - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("statetransitiontimelog", "STATE TRANSITION TIME LOG", PageWidget.WidgetType.STATE_TRANSITION_TIME_LOG)
                        .addWidgetConfigs("flexiblewebstatetransitiontimelog_30","Time Log - 30", 30, PagesContext.PageLayoutType.WEB)
                    .done()
                .getWidgets();
    }
}


