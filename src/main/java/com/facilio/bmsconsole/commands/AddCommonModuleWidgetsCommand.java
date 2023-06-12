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
                        .addWidgetConfigs("flexiblewebsummaryfieldswidget_41","Summary Fields Widget - 41", 41, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilesummaryfieldswidget_8","Summary Fields Widget - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("widgetgroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP)
                        .addWidgetConfigs("flexiblewebwidgetgroup_17","Widget Group - 17", 17, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilewidgetgroup_8","Widget Group - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("comment", "Comment", PageWidget.WidgetType.COMMENT)
                        .addWidgetConfigs("flexiblewebcomment_10","Comment - 10", 10, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilecomment_8","Comment - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("attachment", "Attachment", PageWidget.WidgetType.ATTACHMENT)
                        .addWidgetConfigs("flexiblewebattachment_10","Attachment - 10",10, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobileattachment_8","Attachment - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("classification", "Classfication", PageWidget.WidgetType.CLASSIFICATION)
                        .addWidgetConfigs("flexiblewebclassification_28","Classfication - 28", 28, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobileclassification_8","Classfication - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("activity", "Activity", PageWidget.WidgetType.ACTIVITY)
                        .addWidgetConfigs("flexiblewebactivity_21","Activity - 21",21, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobileactivity_4","Activity - 4",4, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("failurereport", "Failure Report", PageWidget.WidgetType.FAILURE_REPORT)
                        .addWidgetConfigs("flexiblewebfailurereport_21","Failure Report - 21", 21, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilefailurereport_8","Failure Report - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("bulkrelatedlist", "Bulk Related List", PageWidget.WidgetType.BULK_RELATED_LIST)
                        .addWidgetConfigs("flexiblewebbulkrelatedlist_25","Bulk Related List - 25",25, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilebulkrelatedlist_8","Bulk Related List - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("bulkrelationshipwidget", "Bulk Relationship Widget", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET)
                        .addWidgetConfigs("flexiblewebbulkrelationshipwidget_25","Bulk Relationship Widget - 25",25, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilebulkrelationshipwidget_8","Bulk Relationship Widget - 8",8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                    .addModuleWidget("statetransitiontimelog", "STATE TRANSITION TIME LOG", PageWidget.WidgetType.STATE_TRANSITION_TIME_LOG)
                        .addWidgetConfigs("flexiblewebstatetransitiontimelog_46","TIME LOG - 46", 46, PagesContext.PageLayoutType.WEB)
                        .addWidgetConfigs("flexiblemobilestatetransitiontimelog_8","TIME LOG - 8", 8, PagesContext.PageLayoutType.MOBILE)
                    .done()
                .getWidgets();
    }
}


