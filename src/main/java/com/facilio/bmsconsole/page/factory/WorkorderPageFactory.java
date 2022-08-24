package com.facilio.bmsconsole.page.factory;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.RoleFactory;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsoleV3.context.workordersurvey.WorkOrderSurveyResponseContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class WorkorderPageFactory extends PageFactory {
    private static final Logger LOGGER = LogManager.getLogger(WorkorderPageFactory.class.getName());

    private static boolean isTutenLabs() {
        final long ordId = 592L;
        return AccountUtil.getCurrentOrg().getOrgId() == ordId;
    }

    private static void composeRightPanel(Page.Section section, WorkOrderContext workorder) throws Exception {

        if (isTutenLabs()) {
            composeRightPanelTuten(section, workorder);
        } else {
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
                widgetHeight = QuotationAPI.getQuoteCount(workorder.getId()) > 0 ? 8 : 4;
                PageWidget totalCost = new PageWidget(PageWidget.WidgetType.QUOTATION);
                totalCost.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
                totalCost.addToWidgetParams("hideBg", true);
                section.addWidget(totalCost);
                yOffset += widgetHeight;
            }

            // tenant widget
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS) &&
                    workorder.getTenant() != null) {
                widgetHeight = 4;
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
    }
    public static void composeRightPanelTuten(Page.Section section, WorkOrderContext workorder)throws Exception{
        int yOffset = 0;
        int xOffset = 17;
        int widgetWidth = 7;

        // work duration widget
        int widgetHeight = 3;
        PageWidget slaRemainingDuration = new PageWidget(PageWidget.WidgetType.SLA_REMAINING_TIME);
        slaRemainingDuration.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
        slaRemainingDuration.addToWidgetParams("dateField", "responseDueDate");
        section.addWidget(slaRemainingDuration);
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

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
            widgetHeight = QuotationAPI.getQuoteCount(workorder.getId()) > 0 ? 8 : 4;
            PageWidget totalCost = new PageWidget(PageWidget.WidgetType.QUOTATION);
            totalCost.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
            totalCost.addToWidgetParams("hideBg", true);
            section.addWidget(totalCost);
            yOffset += widgetHeight;
        }

        // tenant widget
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS) &&
                workorder.getTenant() != null) {
            widgetHeight = 4;
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

        // scheduled duration widget
        PageWidget scheduledDuration = new PageWidget(PageWidget.WidgetType.SCHEDULED_DURATION);
        scheduledDuration.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
        section.addWidget(scheduledDuration);
        yOffset += widgetHeight;

        // actual duration widget
        PageWidget actualDuration = new PageWidget(PageWidget.WidgetType.ACTUAL_DURATION);
        actualDuration.addToLayoutParams(xOffset, yOffset, widgetWidth, widgetHeight);
        section.addWidget(actualDuration);

    }

    private static void addSummaryTab(Page page, WorkOrderContext workorder) throws Exception {
        if (isTutenLabs()) {
            SummaryTabTuten(page, workorder);
        } else {
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
            commentsWidget.addToLayoutParams(0, 6 + yOffset, 17, 9);
            summarySection.addWidget(commentsWidget);

            // attachments widget
            PageWidget attachmentsWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_ATTACHMENTS);
            attachmentsWidget.addToLayoutParams(0, 15 + yOffset, 17, 8);
            summarySection.addWidget(attachmentsWidget);

            composeRightPanel(summarySection, workorder);
        }
    }

    public static void SummaryTabTuten(Page page, WorkOrderContext workorder) throws Exception {
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

        PageWidget detailsWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_SECONDARY_DETAILS_WIDGET);
        detailsWidget.addToLayoutParams(0, yOffset, 17, 7);
        summarySection.addWidget(detailsWidget);

        PageWidget commentsWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_COMMENTS);
        commentsWidget.addToLayoutParams(0, 6 + yOffset, 17, 9);
        summarySection.addWidget(commentsWidget);

        // attachments widget
        PageWidget attachmentsWidget = new PageWidget(PageWidget.WidgetType.WORKORDER_ATTACHMENTS);
        attachmentsWidget.addToLayoutParams(0, 15 + yOffset, 17, 8);
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

    private static void addFailureReportTab(Page page){
        Page.Tab failureReportTab = page.new Tab("Failure Report");
        page.addTab(failureReportTab);

        Page.Section failureReportSection = page.new Section();
        failureReportTab.addSection(failureReportSection);

        PageWidget failureReport = new PageWidget(PageWidget.WidgetType.FAILURE_REPORT);
        failureReport.addToLayoutParams(failureReportSection, 24, 8);
        failureReport.addToWidgetParams("card", "failurereports");
        failureReportSection.addWidget(failureReport);
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
        Page.Tab relatedRecordsTab = page.new Tab("Related");
        addRelationshipSection(page, relatedRecordsTab, workorderModuleID);
        page.addTab(relatedRecordsTab);

        Page.Section relatedRecordsSection = getRelatedListSectionObj(page);
        relatedRecordsTab.addSection(relatedRecordsSection);

        // related records widget
        PageWidget relatedRecords = new PageWidget(PageWidget.WidgetType.RELATED_RECORDS);
        relatedRecords.addToLayoutParams(relatedRecordsSection, 24, 8);
        relatedRecordsSection.addWidget(relatedRecords);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(workorderModuleID);

        addRelatedListWidgets(relatedRecordsSection, module.getModuleId());
    }

    private static void addMetricandTimelogTab(Page page, long workOrderId) throws Exception {
        Page.Tab metricandTimelogTab = page.new Tab("timelog");
        page.addTab(metricandTimelogTab);

        addWorkOrderSurveyPageWidget(page, metricandTimelogTab,workOrderId);

        Page.Section metrictimelogSection = page.new Section();
        metricandTimelogTab.addSection(metrictimelogSection);

        // metric and timelog widget
        PageWidget stateTransitionTimelogWidget = new PageWidget(PageWidget.WidgetType.STATE_TRANSITION_TIME_LOG);
        stateTransitionTimelogWidget.addToLayoutParams(metrictimelogSection, 24, 8);
        metrictimelogSection.addWidget(stateTransitionTimelogWidget);

    }

    private static void addWorkOrderSurveyPageWidget(Page page, Page.Tab metricandTimelogTab, long workOrderId) throws Exception{

        if(isSurveyAvailable(workOrderId) && AccountUtil.getCurrentUser().getRole().getName().equals(RoleFactory.Role.SUPER_ADMIN.getName())){

            Page.Section surveyTimeLogSection = page.new Section();
            metricandTimelogTab.addSection(surveyTimeLogSection);

            PageWidget surveyTimelogWidget = new PageWidget(PageWidget.WidgetType.SURVEY_RESPONSE_WIDGET);
            surveyTimelogWidget.addToLayoutParams(surveyTimeLogSection, 24, 8);
            surveyTimeLogSection.addWidget(surveyTimelogWidget);
        }
    }

    private static boolean isSurveyAvailable(long workOrderId) throws Exception{

        ModuleBean bean = Constants.getModBean();
        FacilioModule module = bean.getModule(FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE);
        List<FacilioField> fields  = bean.getAllFields(module.getName());

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<WorkOrderSurveyResponseContext> builder = new SelectRecordsBuilder<WorkOrderSurveyResponseContext>()
                .select(fields)
                .moduleName(module.getName())
                .beanClass(WorkOrderSurveyResponseContext.class)
                .andCondition(CriteriaAPI.getCondition(module.getTableName()+".PARENT_ID",module.getTableName()+".parentId",String.valueOf(workOrderId), StringOperators.IS));
        if(!AccountUtil.getCurrentUser().isSuperAdmin()){
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("assignedTo"),String.valueOf(AccountUtil.getCurrentUser().getPeopleId()), NumberOperators.EQUALS));
        }


        return builder.get().size() > 0;
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
        prerequisites.addToLayoutParams(0, 0, 24, 16);
        prerequisites.addToWidgetParams("hideBg", true);
        prerequisiteSection.addWidget(prerequisites);
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
        addMetricandTimelogTab(page, workorder.getId());
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FAILURE_CODES) &&
                workorder.getFailureClass() != null) {
            addFailureReportTab(page);
        }
        addHistoryTab(page);
        return page;
    }
}