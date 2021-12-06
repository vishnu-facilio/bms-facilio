package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class WorkorderPageFactory extends PageFactory {
    private static final Logger LOGGER = LogManager.getLogger(WorkorderPageFactory.class.getName());

    private static void composeRightPanel(Page.Section section, WorkOrderContext workorder) throws Exception {

        int yOffset = 0;
        int xOffset = 17;
        int widgetWidth = 7;

        // work duration widget
        int widgetHeight = 3;
        PageWidget workDuration = new PageWidget(PageWidget.WidgetType.WORK_DURATION);
        workDuration.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
        section.addWidget(workDuration);
        yOffset += widgetHeight;

        // total cost widget
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY) &&
                !AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
            widgetHeight = 3;
            PageWidget totalCost = new PageWidget(PageWidget.WidgetType.TOTAL_COST);
            totalCost.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
            section.addWidget(totalCost);
            yOffset += widgetHeight;
        }

        // resource widget
        widgetHeight = 3;
        PageWidget resource = new PageWidget(PageWidget.WidgetType.RESOURCE);
        resource.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
        section.addWidget(resource);
        yOffset += widgetHeight;

        // maintenance cost & quotation widget
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
            widgetHeight = QuotationAPI.getQuoteCount(workorder.getId()) > 0 ? 8 : 3;
            PageWidget totalCost = new PageWidget(PageWidget.WidgetType.QUOTATION);
            totalCost.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
            totalCost.addToWidgetParams("hideBg", true);
            section.addWidget(totalCost);
            yOffset += widgetHeight;
        }

        // tenant widget
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS) &&
                workorder.getTenant() != null) {
            widgetHeight = 3;
            PageWidget totalCost = new PageWidget(PageWidget.WidgetType.TENANT);
            totalCost.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
            section.addWidget(totalCost);
            yOffset += widgetHeight;
        }

        // responsibility widget
        widgetHeight = 6;
        PageWidget responsibility = new PageWidget(PageWidget.WidgetType.RESPONSIBILITY);
        responsibility.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
        section.addWidget(responsibility);
        yOffset += widgetHeight;

        // workorderDetails widget
        widgetHeight = 10;
        PageWidget workorderDetails = new PageWidget(PageWidget.WidgetType.WORKORDER_DETAILS);
        workorderDetails.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
        section.addWidget(workorderDetails);
        yOffset += widgetHeight;
    }

    private static void addSummaryTab(Page page, WorkOrderContext workorder) throws Exception {
        Page.Tab summaryTab = page.new Tab("summary");
        page.addTab(summaryTab);

        Page.Section summarySection = page.new Section();
        summaryTab.addSection(summarySection);

        int yOffset = 0;

        if (workorder.getDescription() != null && !workorder.getDescription().isEmpty()) {
            PageWidget descWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_DESCRIPTION);
            descWidget.addToLayoutParams(0, 0, 17, 3);
            summarySection.addWidget(descWidget);
            yOffset += 3;
        }

        // tasks completed widget
        PageWidget tasksCompleted = new PageWidget(PageWidget.WidgetType.TASKS_COMPLETED);
        tasksCompleted.addToLayoutParams(0, yOffset, 5, 6);
        summarySection.addWidget(tasksCompleted);

        // scheduled duration widget
        PageWidget scheduledDuration = new PageWidget(PageWidget.WidgetType.SCHEDULED_DURATION);
        scheduledDuration.addToLayoutParams(5, yOffset, 6, 6);
        summarySection.addWidget(scheduledDuration);

        // actual duration widget
        PageWidget actualDuration = new PageWidget(PageWidget.WidgetType.ACTUAL_DURATION);
        actualDuration.addToLayoutParams(11, yOffset, 6, 6);
        summarySection.addWidget(actualDuration);

        // comments widget
        PageWidget commentsWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_COMMENTS);
        commentsWidget.addToLayoutParams(0, 6 + yOffset, 17, 8);
        summarySection.addWidget(commentsWidget);

        // attachments widget
        PageWidget attachmentsWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_ATTACHMENTS);
        attachmentsWidget.addToLayoutParams(0, 14 + yOffset, 17, 8);
        summarySection.addWidget(attachmentsWidget);

        composeRightPanel(summarySection, workorder);
    }

    private static void addInventoryTab(Page page) throws Exception {
        Page.Tab itemsAndLaborTab = page.new Tab("items & labor");
        page.addTab(itemsAndLaborTab);

        Page.Section itemsAndLaborSection = page.new Section();
        itemsAndLaborTab.addSection(itemsAndLaborSection);

        int yOffset = 0;

        // overall cost
        PageWidget overallCost = new PageWidget(PageWidget.WidgetType.INVENTORY_OVERALL_COST);
        overallCost.addToLayoutParams(16, 0, 8, 10);
        itemsAndLaborSection.addWidget(overallCost);

        // items
        PageWidget items = new PageWidget(PageWidget.WidgetType.INVENTORY_ITEMS);
        items.addToLayoutParams(itemsAndLaborSection, 16, 7 + yOffset);
        itemsAndLaborSection.addWidget(items);
        yOffset += 7;

        // services
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONTRACT)) {
            PageWidget services = new PageWidget(PageWidget.WidgetType.INVENTORY_SERVICES);
            services.addToLayoutParams(0, yOffset, 16, 7);
            itemsAndLaborSection.addWidget(services);
            yOffset += 7;
        }

        // labor
        PageWidget labor = new PageWidget(PageWidget.WidgetType.INVENTORY_LABOR);
        labor.addToLayoutParams(0, yOffset, 16, 7);
        itemsAndLaborSection.addWidget(labor);
        yOffset += 7;

        // tools
        PageWidget tools = new PageWidget(PageWidget.WidgetType.INVENTORY_TOOLS);
        tools.addToLayoutParams(0, yOffset, 16, 7);
        itemsAndLaborSection.addWidget(tools);
        yOffset += 7;
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

    private static void addRelatedRecordsTab(Page page, long workorderModuleID) throws Exception {
        Page.Tab relatedRecordsTab = page.new Tab("related records");
        page.addTab(relatedRecordsTab);

        Page.Section relatedRecordsSection = page.new Section();
        relatedRecordsTab.addSection(relatedRecordsSection);

        // related records widget
        PageWidget relatedRecords = new PageWidget(PageWidget.WidgetType.RELATED_RECORDS);
        relatedRecords.addToLayoutParams(relatedRecordsSection, 24, 8);
        relatedRecordsSection.addWidget(relatedRecords);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(workorderModuleID);

        addRelatedListWidgets(relatedRecordsSection, module.getModuleId());
    }

    private static void addTasksTab(Page page) {
        Page.Tab tasksTab = page.new Tab("tasks");
        page.addTab(tasksTab);

        Page.Section tasksSection = page.new Section();
        tasksTab.addSection(tasksSection);

        // tasks monolith widget
        PageWidget tasksMonolith = new PageWidget(PageWidget.WidgetType.TASKS_MONOLITH);
        tasksMonolith.addToLayoutParams(tasksSection, 24, 18);
        tasksMonolith.addToWidgetParams("hideBg", true);
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

    private static Page.Section addSafetyPlanTab(Page page, WorkOrderContext workorder) throws Exception {
        Page.Tab safetyPlanTab = page.new Tab("safety plan");
        page.addTab(safetyPlanTab);

        Page.Section safetyPlanSection = page.new Section();
        safetyPlanTab.addSection(safetyPlanSection);

        // hazards widget
        PageWidget hazards = new PageWidget(PageWidget.WidgetType.HAZARDS);
        hazards.addToLayoutParams(0, 0, 17, 8);
        safetyPlanSection.addWidget(hazards);

        // precautions widget
        PageWidget precautions = new PageWidget(PageWidget.WidgetType.PRECAUTIONS);
        precautions.addToLayoutParams(0, 8, 17, 8);
        safetyPlanSection.addWidget(precautions);

        composeRightPanel(safetyPlanSection, workorder);
        return safetyPlanSection;
    }

    private static void addSafetyPlanTabWithPrerequisites(Page page, WorkOrderContext workorder) throws Exception {
        Page.Section safetyPlanSection = addSafetyPlanTab(page, workorder);

        // prerequisites widget
        PageWidget prerequisites = new PageWidget(PageWidget.WidgetType.PREREQUISITES);
        prerequisites.addToLayoutParams(0, 16, 18, 10);
        safetyPlanSection.addWidget(prerequisites);
    }

    private static void addPrerequisiteTab(Page page, WorkOrderContext workorder) throws Exception {
        Page.Tab prerequisite = page.new Tab("prerequisites");
        page.addTab(prerequisite);

        Page.Section prerequisiteSection = page.new Section();
        prerequisite.addSection(prerequisiteSection);

        // prerequisites widget
        PageWidget prerequisites = new PageWidget(PageWidget.WidgetType.PREREQUISITES);
        prerequisites.addToLayoutParams(0, 0, 18, 16);
        prerequisiteSection.addWidget(prerequisites);

        composeRightPanel(prerequisiteSection, workorder);
    }

    public static Page getWorkorderPage(WorkOrderContext workorder) throws Exception {
        Page page = new Page();

        addSummaryTab(page, workorder);

        // isPrerequisiteEnabled is true when WO generated as a part of PM & Safety Plan is not enabled
        // when Safety Plan is enabled, prerequisites are shown as a widget in Safety Plan tab
        if (workorder.isPrerequisiteEnabled()) {
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
                // SP & PR
                addSafetyPlanTabWithPrerequisites(page, workorder);
            } else {
                // !SP & PR
                addPrerequisiteTab(page, workorder);
            }
        } else if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
            // SP & !PR
            addSafetyPlanTab(page, workorder);
        }
        addTasksTab(page);
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
            addInventoryTab(page);
        }
        addRelatedRecordsTab(page, workorder.getModuleId());
        addHistoryTab(page);
        return page;
    }
}
