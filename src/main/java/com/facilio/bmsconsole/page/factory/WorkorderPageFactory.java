package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class WorkorderPageFactory extends PageFactory {
    private static final Logger LOGGER = LogManager.getLogger(WorkorderPageFactory.class.getName());

    private static void addSummaryTab(Page page) {
        Page.Tab summaryTab = page.new Tab("summary");
        page.addTab(summaryTab);

        // TODO:: give a more contextual name for the section
        Page.Section sectionOne = page.new Section();
        summaryTab.addSection(sectionOne);

        // tasks completed widget
        PageWidget tasksCompleted = new PageWidget(PageWidget.WidgetType.TASKS_COMPLETED);
        tasksCompleted.addToLayoutParams(sectionOne, 6, 6);
        sectionOne.addWidget(tasksCompleted);

        // scheduled duration widget
        PageWidget scheduledDuration = new PageWidget(PageWidget.WidgetType.SCHEDULED_DURATION);
        scheduledDuration.addToLayoutParams(sectionOne, 6, 6);
        sectionOne.addWidget(scheduledDuration);

        // actual duration widget
        PageWidget actualDuration = new PageWidget(PageWidget.WidgetType.ACTUAL_DURATION);
        actualDuration.addToLayoutParams(sectionOne, 6, 6);
        sectionOne.addWidget(actualDuration);

        // work duration widget
        PageWidget workDuration = new PageWidget(PageWidget.WidgetType.WORK_DURATION);
        workDuration.addToLayoutParams(18, 0, 6, 3);
        sectionOne.addWidget(workDuration);

        // resource widget
        PageWidget resource = new PageWidget(PageWidget.WidgetType.RESOURCE);
        resource.addToLayoutParams(18, 3, 6, 3);
        sectionOne.addWidget(resource);

        // responsibility widget
        PageWidget responsibility = new PageWidget(PageWidget.WidgetType.RESPONSIBILITY);
        responsibility.addToLayoutParams(18, 6, 6, 6);
        sectionOne.addWidget(responsibility);

        // workorderDetails widget
        PageWidget workorderDetails = new PageWidget(PageWidget.WidgetType.WORKORDER_DETAILS);
        workorderDetails.addToLayoutParams(18, 12, 6, 12);
        sectionOne.addWidget(workorderDetails);

        // notes widget
        PageWidget notesWidget = new PageWidget(PageWidget.WidgetType.COMMENT);
        notesWidget.addToLayoutParams(0, 6, 18, 8);
        sectionOne.addWidget(notesWidget);

        // attachment widget
        PageWidget attachmentsWidget = new PageWidget(PageWidget.WidgetType.ATTACHMENT);
        attachmentsWidget.addToLayoutParams(0, 14, 18, 8);
        sectionOne.addWidget(attachmentsWidget);
    }

    private static void addHistoryTab(Page page) {
        Page.Tab historyPage = page.new Tab("history");
        page.addTab(historyPage);

        // TODO:: give a more contextual name for the section
        Page.Section sectionOne = page.new Section();
        historyPage.addSection(sectionOne);

        // history widget
        PageWidget historyWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_HISTORY);
        historyWidget.addToLayoutParams(sectionOne, 24, 18);
        sectionOne.addWidget(historyWidget);
    }

    private static void addRelatedRecordsTab(Page page) {
        Page.Tab relatedRecordsTab = page.new Tab("relatedRecords");
        page.addTab(relatedRecordsTab);

        // TODO:: give a more contextual name for the section
        Page.Section sectionOne = page.new Section();
        relatedRecordsTab.addSection(sectionOne);

        // related records widget
        PageWidget relatedRecords = new PageWidget(PageWidget.WidgetType.RELATED_RECORDS);
        relatedRecords.addToLayoutParams(sectionOne, 24, 12);
        sectionOne.addWidget(relatedRecords);
    }

    private static void addTasksTab(Page page) {
        Page.Tab tasksTab = page.new Tab("tasks");
        page.addTab(tasksTab);

        // TODO:: give a more contextual name for the section
        Page.Section sectionOne = page.new Section();
        tasksTab.addSection(sectionOne);

        // workorder progress widget
        PageWidget workorderProgress = new PageWidget(PageWidget.WidgetType.WORKORDER_PROGRESS);
        workorderProgress.addToLayoutParams(sectionOne, 18, 2);
        sectionOne.addWidget(workorderProgress);

        // tasks widget
        PageWidget tasks = new PageWidget(PageWidget.WidgetType.TASKS);
        tasks.addToLayoutParams(0, 2, 18, 8);
        sectionOne.addWidget(tasks);

        // sideBar widget
        PageWidget sideBar = new PageWidget(PageWidget.WidgetType.WORKORDER_SIDEBAR);
        sideBar.addToLayoutParams(18, 0, 6, 18);
        sectionOne.addWidget(sideBar);
    }

    private static void addSafetyPlanTab(Page page) {
        Page.Tab safetyPlanTab = page.new Tab("safety plan");
        page.addTab(safetyPlanTab);

        // TODO:: give a more contextual name for the section
        Page.Section sectionOne = page.new Section();
        safetyPlanTab.addSection(sectionOne);

        // hazards widget
        PageWidget hazards = new PageWidget(PageWidget.WidgetType.HAZARDS);
        hazards.addToLayoutParams(0, 0, 18, 8);
        sectionOne.addWidget(hazards);

        // precautions widget
        PageWidget precautions = new PageWidget(PageWidget.WidgetType.PRECAUTIONS);
        precautions.addToLayoutParams(0, 8, 18, 8);
        sectionOne.addWidget(precautions);

        // work duration widget
        PageWidget workDuration = new PageWidget(PageWidget.WidgetType.WORK_DURATION);
        workDuration.addToLayoutParams(18, 0, 6, 3);
        sectionOne.addWidget(workDuration);

        // resource widget
        PageWidget resource = new PageWidget(PageWidget.WidgetType.RESOURCE);
        resource.addToLayoutParams(18, 3, 6, 3);
        sectionOne.addWidget(resource);

        // responsibility widget
        PageWidget responsibility = new PageWidget(PageWidget.WidgetType.RESPONSIBILITY);
        responsibility.addToLayoutParams(18, 6, 6, 6);
        sectionOne.addWidget(responsibility);

        // workorderDetails widget
        PageWidget workorderDetails = new PageWidget(PageWidget.WidgetType.WORKORDER_DETAILS);
        workorderDetails.addToLayoutParams(18, 12, 6, 12);
        sectionOne.addWidget(workorderDetails);
    }

    public static Page getWorkorderPage(WorkOrderContext workorder) throws Exception {
        Page page = new Page();
        addSummaryTab(page);
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
            addSafetyPlanTab(page);
        }
        addTasksTab(page);
        addRelatedRecordsTab(page);
        addHistoryTab(page);
        return page;
    }
}
