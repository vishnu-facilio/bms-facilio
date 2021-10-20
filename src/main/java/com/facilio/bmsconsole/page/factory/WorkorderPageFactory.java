package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class WorkorderPageFactory extends PageFactory {
    private static final Logger LOGGER = LogManager.getLogger(WorkorderPageFactory.class.getName());

    private static void composeRightPanel(Page.Section section) throws Exception {

        int yOffset = 0;

        // work duration widget
        PageWidget workDuration = new PageWidget(PageWidget.WidgetType.WORK_DURATION);
        workDuration.addToLayoutParams(18, 0, 6, 3);
        section.addWidget(workDuration);

        // total cost widget
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
            PageWidget totalCost = new PageWidget(PageWidget.WidgetType.TOTAL_COST);
            totalCost.addToLayoutParams(18, 3, 6, 3);
            section.addWidget(totalCost);
            yOffset += 3;
        }

        // maintenance cost widget
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
            PageWidget totalCost = new PageWidget(PageWidget.WidgetType.QUOTATION);
            totalCost.addToLayoutParams(18, 3, 6, 3);
            section.addWidget(totalCost);
            yOffset += 3;
        }

        // resource widget
        PageWidget resource = new PageWidget(PageWidget.WidgetType.RESOURCE);
        resource.addToLayoutParams(18, 3 + yOffset, 6, 3);
        section.addWidget(resource);

        // responsibility widget
        PageWidget responsibility = new PageWidget(PageWidget.WidgetType.RESPONSIBILITY);
        responsibility.addToLayoutParams(18, 6 + yOffset, 6, 6);
        section.addWidget(responsibility);

        // workorderDetails widget
        PageWidget workorderDetails = new PageWidget(PageWidget.WidgetType.WORKORDER_DETAILS);
        workorderDetails.addToLayoutParams(18, 12 + yOffset, 6, 12);
        section.addWidget(workorderDetails);
    }

    private static void addSummaryTab(Page page, boolean withDescription) throws Exception {
        Page.Tab summaryTab = page.new Tab("summary");
        page.addTab(summaryTab);

        Page.Section summarySection = page.new Section();
        summaryTab.addSection(summarySection);

        int yOffset = 0;
        if (withDescription) {
            PageWidget descWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_DESCRIPTION);
            descWidget.addToLayoutParams(0, 0, 18, 3);
            summarySection.addWidget(descWidget);
            yOffset += 3;
        }

        // tasks completed widget
        PageWidget tasksCompleted = new PageWidget(PageWidget.WidgetType.TASKS_COMPLETED);
        tasksCompleted.addToLayoutParams(0, yOffset, 6, 6);
        summarySection.addWidget(tasksCompleted);

        // scheduled duration widget
        PageWidget scheduledDuration = new PageWidget(PageWidget.WidgetType.SCHEDULED_DURATION);
        scheduledDuration.addToLayoutParams(6, yOffset, 6, 6);
        summarySection.addWidget(scheduledDuration);

        // actual duration widget
        PageWidget actualDuration = new PageWidget(PageWidget.WidgetType.ACTUAL_DURATION);
        actualDuration.addToLayoutParams(12, yOffset, 6, 6);
        summarySection.addWidget(actualDuration);

        // comments widget
        PageWidget commentsWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_COMMENTS);
        commentsWidget.addToLayoutParams(0, 6 + yOffset, 18, 8);
        summarySection.addWidget(commentsWidget);

        // attachments widget
        PageWidget attachmentsWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_ATTACHMENTS);
        attachmentsWidget.addToLayoutParams(0, 14 + yOffset, 18, 8);
        summarySection.addWidget(attachmentsWidget);

        composeRightPanel(summarySection);
    }

    private static void addInventoryTab(Page page) {
        Page.Tab itemsAndLaborTab = page.new Tab("items & labor");
        page.addTab(itemsAndLaborTab);

        Page.Section itemsAndLaborSection = page.new Section();
        itemsAndLaborTab.addSection(itemsAndLaborSection);

        // TODO::VR monolith, to be decomposed.
        // items & labor widget
        PageWidget itemsAndLabor = new PageWidget(PageWidget.WidgetType.ITEMS_AND_LABOR);
        itemsAndLabor.addToLayoutParams(itemsAndLaborSection, 24, 18);
        itemsAndLaborSection.addWidget(itemsAndLabor);
    }

    private static void addHistoryTab(Page page) {
        Page.Tab historyPage = page.new Tab("history");
        page.addTab(historyPage);

        Page.Section historySection = page.new Section();
        historyPage.addSection(historySection);

        // history widget
        PageWidget historyWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_HISTORY);
        historyWidget.addToLayoutParams(historySection, 24, 18);
        historySection.addWidget(historyWidget);
    }

    private static void addRelatedRecordsTab(Page page) {
        Page.Tab relatedRecordsTab = page.new Tab("related records");
        page.addTab(relatedRecordsTab);

        Page.Section relatedRecordsSection = page.new Section();
        relatedRecordsTab.addSection(relatedRecordsSection);

        // related records widget
        PageWidget relatedRecords = new PageWidget(PageWidget.WidgetType.RELATED_RECORDS);
        relatedRecords.addToLayoutParams(relatedRecordsSection, 24, 12);
        relatedRecordsSection.addWidget(relatedRecords);
    }

    private static void addTasksTab(Page page) {
        Page.Tab tasksTab = page.new Tab("tasks");
        page.addTab(tasksTab);

        Page.Section tasksSection = page.new Section();
        tasksTab.addSection(tasksSection);

        // tasks monolith widget
        PageWidget tasksMonolith = new PageWidget(PageWidget.WidgetType.TASKS_MONOLITH);
        tasksMonolith.addToLayoutParams(tasksSection, 24, 18);
        tasksSection.addWidget(tasksMonolith);

//        // workorder progress widget
//        PageWidget workorderProgress = new PageWidget(PageWidget.WidgetType.WORKORDER_PROGRESS);
//        workorderProgress.addToLayoutParams(tasksSection, 18, 2);
//        tasksSection.addWidget(workorderProgress);
//
//        // tasks widget
//        PageWidget tasks = new PageWidget(PageWidget.WidgetType.TASKS);
//        tasks.addToLayoutParams(0, 2, 18, 8);
//        tasksSection.addWidget(tasks);
//
//        composeRightPanel(tasksSection);
    }

    private static Page.Section addSafetyPlanTab(Page page) throws Exception {
        Page.Tab safetyPlanTab = page.new Tab("safety plan");
        page.addTab(safetyPlanTab);

        Page.Section safetyPlanSection = page.new Section();
        safetyPlanTab.addSection(safetyPlanSection);

        // hazards widget
        PageWidget hazards = new PageWidget(PageWidget.WidgetType.HAZARDS);
        hazards.addToLayoutParams(0, 0, 18, 8);
        safetyPlanSection.addWidget(hazards);

        // precautions widget
        PageWidget precautions = new PageWidget(PageWidget.WidgetType.PRECAUTIONS);
        precautions.addToLayoutParams(0, 8, 18, 8);
        safetyPlanSection.addWidget(precautions);

        composeRightPanel(safetyPlanSection);
        return safetyPlanSection;
    }

    private static void addSafetyPlanTabWithPrerequisites(Page page) throws Exception {
        Page.Section safetyPlanSection = addSafetyPlanTab(page);

        // prerequisites widget
        PageWidget prerequisites = new PageWidget(PageWidget.WidgetType.PREREQUISITES);
        prerequisites.addToLayoutParams(0, 16, 18, 10);
        safetyPlanSection.addWidget(prerequisites);
    }

    private static void addPrerequisiteTab(Page page) throws Exception {
        Page.Tab prerequisite = page.new Tab("prerequisites");
        page.addTab(prerequisite);

        Page.Section prerequisiteSection = page.new Section();
        prerequisite.addSection(prerequisiteSection);

        // prerequisites widget
        PageWidget prerequisites = new PageWidget(PageWidget.WidgetType.PREREQUISITES);
        prerequisites.addToLayoutParams(0, 0, 18, 16);
        prerequisiteSection.addWidget(prerequisites);

        composeRightPanel(prerequisiteSection);
    }

    public static Page getWorkorderPage(WorkOrderContext workorder) throws Exception {
        Page page = new Page();

        boolean withDesc = workorder.getDescription() != null && !workorder.getDescription().isEmpty();
        addSummaryTab(page, withDesc);

        // isPrerequisiteEnabled is true when WO generated as a part of PM & Safety Plan is not enabled
        // when Safety Plan is enabled, prerequisites are shown as a widget in Safety Plan tab
        if (workorder.isPrerequisiteEnabled()) {
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
                // SP & PR
                addSafetyPlanTabWithPrerequisites(page);
            } else {
                // !SP & PR
                addPrerequisiteTab(page);
            }
        } else if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
            // SP & !PR
            addSafetyPlanTab(page);
        }
        addTasksTab(page);
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
            addInventoryTab(page);
        }
        addRelatedRecordsTab(page);
        addHistoryTab(page);
        return page;
    }
}
