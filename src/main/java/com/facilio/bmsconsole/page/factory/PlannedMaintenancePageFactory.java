package com.facilio.bmsconsole.page.factory;

import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.WidgetGroup;
import com.facilio.constants.FacilioConstants;

public class PlannedMaintenancePageFactory extends PageFactory{

    public static Page getPlannedMaintenancePage(PlannedMaintenance record) throws Exception {
        Page page = new Page();

        // Summary Tab
        addSummaryTab(page);

        // Planner Tab
        addPlannerTab(page);

        // Executed WorkOrders
        //addExecutedWorkOrdersTab(page);

        // Scheduler
        //addSchedulerTab(page);

        // History
        //addHistoryTab(page);

        return page;
    }


    private static void addSummaryTab(Page page){
        Page.Tab summaryTab = page.new Tab("Summary");
        page.addTab(summaryTab);

        Page.Section summaryTabSection = page.new Section();
        summaryTab.addSection(summaryTabSection);

        PageWidget pmDetailsWidget = new PageWidget(PageWidget.WidgetType.PLANNED_MAINTENANCE_DETAILS);
        pmDetailsWidget.addToLayoutParams(summaryTabSection, 24, 5);
        pmDetailsWidget.addToWidgetParams("card", "plannedMaintenanceDetails");
        summaryTabSection.addWidget(pmDetailsWidget);

        PageWidget pmSecondaryDetailsWidget =  new PageWidget(PageWidget.WidgetType.PLANNED_MAINTENANCE_DETAILS);
        pmSecondaryDetailsWidget.addToLayoutParams(summaryTabSection, 24, 11);
        pmSecondaryDetailsWidget.addToWidgetParams("card", "plannedMaintenanceSecondaryDetails");
        summaryTabSection.addWidget(pmSecondaryDetailsWidget);

        // add Notes and Attachments widgets as grouped widget
        PageWidget subModuleGroup = new PageWidget(PageWidget.WidgetType.GROUP);
        subModuleGroup.addToLayoutParams(summaryTabSection, 24, 8);
        subModuleGroup.addToWidgetParams("type", WidgetGroup.WidgetGroupType.TAB);
        summaryTabSection.addWidget(subModuleGroup);

        PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
        //notesWidget.addToLayoutParams(summaryTabSection, 24, 8);
        subModuleGroup.addToWidget(notesWidget);

        //PageWidget attachmentWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
        //attachmentWidget.addToLayoutParams(summaryTabSection, 24, 8);
        //subModuleGroup.addToWidget(attachmentWidget);
    }

    private static void addPlannerTab(Page page) {
        Page.Tab plannerTab = page.new Tab("Planner");
        page.addTab(plannerTab);

        Page.Section plannerTabSection = page.new Section();
        plannerTab.addSection(plannerTabSection);

        PageWidget pmPlannerDetailsWidget = new PageWidget(PageWidget.WidgetType.PM_PLANNER_DETAILS);
        pmPlannerDetailsWidget.addToLayoutParams(plannerTabSection, 24, 13);
        pmPlannerDetailsWidget.addToWidgetParams("card", "pmPlannerDetails");
        plannerTabSection.addWidget(pmPlannerDetailsWidget);

    }

    private static void addExecutedWorkOrdersTab(Page page) {

        Page.Tab executedWorkOrdersTab = page.new Tab("Executed WorkOrders");
        page.addTab(executedWorkOrdersTab);

        Page.Section executedWorkOrdersSection = page.new Section();
        executedWorkOrdersTab.addSection(executedWorkOrdersSection);

        PageWidget pmExecutedWorkOrdersDetailsWidget = new PageWidget(PageWidget.WidgetType.PM_EXECUTED_WORKORDERS_DETAILS);
        pmExecutedWorkOrdersDetailsWidget.addToLayoutParams(executedWorkOrdersSection, 24, 13);
        pmExecutedWorkOrdersDetailsWidget.addToWidgetParams("card", "pmExecutedWorkOrdersDetails");
        executedWorkOrdersSection.addWidget(pmExecutedWorkOrdersDetailsWidget);
    }

    private static void addSchedulerTab(Page page) {
        Page.Tab schedulerTab = page.new Tab("Scheduler");
        page.addTab(schedulerTab);

        Page.Section schedulerSection = page.new Section();
        schedulerTab.addSection(schedulerSection);

        PageWidget schedulerDetailsWidget = new PageWidget(PageWidget.WidgetType.PM_SCHEDULER_DETAILS);
        schedulerDetailsWidget.addToLayoutParams(schedulerSection, 24, 13);
        schedulerDetailsWidget.addToWidgetParams("card", "pmSchedulerDetails");
        schedulerSection.addWidget(schedulerDetailsWidget);
    }

    private static void addHistoryTab(Page page) {
        Page.Tab historyTab = page.new Tab("History");
        page.addTab(historyTab);

        Page.Section historyTabSection = page.new Section();
        historyTab.addSection(historyTabSection);
        PageWidget activityWidget = new PageWidget(PageWidget.WidgetType.ACTIVITY);
        activityWidget.addToLayoutParams(historyTabSection, 24, 3);
        activityWidget.addToWidgetParams("activityModuleName", FacilioConstants.ContextNames.CUSTOM_ACTIVITY);
        historyTabSection.addWidget(activityWidget);
    }

}
