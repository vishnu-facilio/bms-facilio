package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facilio.util.SummaryWidgetUtil.addSummaryFieldInWidgetGroup;

public class ServicePlannedMaintenanceTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_ACTIVITY);
                return new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryFields", null, null)
                .addWidget("summaryFieldsWidget", "Service PM Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("servicePlan", "Service Plan", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicePlanTasks", "Tasks", null)
                .addWidget("servicePlanTasks", "Tasks", PageWidget.WidgetType.SERVICE_PLAN_TASKS, "webServicePlanTasks_5_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("servicePlanInventory", "Inventory", null)
                .addWidget("servicePlanInventory", "Inventory", PageWidget.WidgetType.SERVICE_PLAN_INVENTORY, "webServicePlanInventory_5_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("serviceOrders", "Work Orders", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceOrders", "Work Orders", null)
                .addWidget("serviceOrders", "Work Orders", PageWidget.WidgetType.SERVICE_PM_SERVICE_ORDERS, "webPMServiceOrders_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("activity", "Activity", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_10", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                ;
    }
    private static JSONObject getSummaryWidgetDetails(ApplicationContext app, String moduleName) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> servicePMFields = modBean.getAllFields(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        Map<String, FacilioField> serviceOrderFieldsMap = FieldFactory.getAsMap(servicePMFields);
        List<FacilioField> servicePMTriggerFields = modBean.getAllFields(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER);
        Map<String, FacilioField> servicePMTriggerFieldsMap = FieldFactory.getAsMap(servicePMTriggerFields);
        FacilioField descriptionField = serviceOrderFieldsMap.get("pmDescription");
        FacilioField resourceTypeField = serviceOrderFieldsMap.get("resourceType");
        FacilioField siteField = serviceOrderFieldsMap.get("site");
        FacilioField assetField = serviceOrderFieldsMap.get("asset");
        FacilioField spaceField = serviceOrderFieldsMap.get("space");
        FacilioField clientField = serviceOrderFieldsMap.get("client");
        FacilioField resolutionDueDurationField = serviceOrderFieldsMap.get("resolutionDueDuration");
        FacilioField leadTimeField = serviceOrderFieldsMap.get("leadTime");
        FacilioField previewPeriodField = serviceOrderFieldsMap.get("previewPeriod");
        FacilioField servicePMTriggerField = serviceOrderFieldsMap.get("servicePMTrigger");
        FacilioField nextRunField = serviceOrderFieldsMap.get("nextRun");
        FacilioField lastRunField = serviceOrderFieldsMap.get("lastRun");
        FacilioField triggerName = servicePMTriggerFieldsMap.get("name");
        FacilioField triggerFrequency = servicePMTriggerFieldsMap.get("frequency");
        FacilioField triggerStartTime = servicePMTriggerFieldsMap.get("startTime");
        FacilioField triggerEndTime = servicePMTriggerFieldsMap.get("endTime");

        SummaryWidget pageWidget = new SummaryWidget();

        // Group 1
        SummaryWidgetGroup servicePMInfoWidgetGroup = new SummaryWidgetGroup();
        servicePMInfoWidgetGroup.setName("servicePMInformation");
        servicePMInfoWidgetGroup.setDisplayName("Service PM Information");
        servicePMInfoWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, descriptionField, 1, 1, 4);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, resourceTypeField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, siteField, 2, 2, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, assetField, 2, 3, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, spaceField, 2, 4, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, clientField, 3, 1, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, resolutionDueDurationField, 3, 2, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, leadTimeField, 3, 3, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, previewPeriodField, 3, 4, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, lastRunField, 4, 1, 1);
        addSummaryFieldInWidgetGroup(servicePMInfoWidgetGroup, nextRunField, 4, 2, 1);
        // Group 2
        SummaryWidgetGroup scheduleInfoWidgetGroup = new SummaryWidgetGroup();
        scheduleInfoWidgetGroup.setName("recurringScheduleInformation");
        scheduleInfoWidgetGroup.setDisplayName("Recurring Schedule Information");
        scheduleInfoWidgetGroup.setColumns(4);

        addSummaryFieldInWidgetGroup(scheduleInfoWidgetGroup,"Schedule Info", triggerName, 1, 1, 4,servicePMTriggerField);
        addSummaryFieldInWidgetGroup(scheduleInfoWidgetGroup,triggerFrequency.getDisplayName(), triggerFrequency, 2, 1, 1,servicePMTriggerField);
        addSummaryFieldInWidgetGroup(scheduleInfoWidgetGroup,triggerStartTime.getDisplayName(), triggerStartTime, 2, 2, 1,servicePMTriggerField);
        addSummaryFieldInWidgetGroup(scheduleInfoWidgetGroup,triggerEndTime.getDisplayName(), triggerEndTime, 2, 3, 1,servicePMTriggerField);



        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(servicePMInfoWidgetGroup);
        widgetGroupList.add(scheduleInfoWidgetGroup);

        pageWidget.setName("servicePMDetails");
        pageWidget.setDisplayName("Service PM Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("activityModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("activityModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 4, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 4, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}
