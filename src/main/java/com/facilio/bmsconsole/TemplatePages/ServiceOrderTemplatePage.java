package com.facilio.bmsconsole.TemplatePages;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceOrderTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule serviceOrderModule = modBean.getModule(FacilioConstants.ContextNames.SERVICE_ORDER);
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_NOTES);
        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ATTACHMENTS);
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ACTIVITY);
        return  new PagesContext(null, null,"", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryFields", null, null)
                .addWidget("summaryFieldsWidget", "Work Order Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,serviceOrderModule.getName()))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("serviceTask", "Task", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("task", null, null)
                .addWidget("taskList", "Tasks", PageWidget.WidgetType.SERVICE_TASK_WIDGET, "webTaskList_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("appointment", "Appointment", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("appointments", null, null)
                .addWidget("appointments", "Appointments", PageWidget.WidgetType.SERVICE_ORDER_APPOINTMENTS, "webAppointments_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("plans", "Plans", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("plans", null, null)
                .addWidget("servicePlans", "Plans", PageWidget.WidgetType.SERVICE_ORDER_PLANS, "webServicePlans_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("actuals", "Actuals", PageTabContext.TabType.SINGLE_WIDGET_TAB, true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("actuals", null, null)
                .addWidget("serviceActuals", "Actuals", PageWidget.WidgetType.SERVICE_ORDER_ACTUALS, "webServiceActuals_10_12", 0, 0, null, null)
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

                .addMobileLayout()

                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summary", null, null)
                .addWidget("summaryFieldsWidget", null, PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblemobilesummaryfieldswidget_14", 0, 0, null, getSummaryWidgetDetails(app,serviceOrderModule.getName()))
                .widgetDone()
                .addWidget("serviceOrderResourceWidget", null, PageWidget.WidgetType.RESOURCE_WIDGET, "mobileFlexibleResourceWidget_6", 0, 15, null, null)
                .widgetDone()
                .addWidget("serviceOrderLocationWidget", null, PageWidget.WidgetType.LOCATION_WIDGET, "mobileFlexibleLocationWidget_9", 0, 21, null, null)
                .widgetDone()
                .addWidget("serviceOrderUserWidget", null, PageWidget.WidgetType.USER_WIDGET, "mobileFlexibleUserWidget_7", 0, 30, null, null)
                .widgetDone()
                .addWidget("attachment", null, PageWidget.WidgetType.ATTACHMENT, "flexiblemobileattachment_8", 0, 37, attachmentWidgetParam, null)
                .widgetDone()
                .addWidget("comment", null, PageWidget.WidgetType.COMMENT, "flexiblemobilecomment_8", 0, 45, commentWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceTask", "Service Task", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceTask", null, null)
                .addWidget("serviceOrderServiceTaskListWidget", null, PageWidget.WidgetType.SERVICE_TASK_LIST_WIDGET, "mobileServiceTaskListWidget_16", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointment", "Service Appointment", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointment", null, null)
                .addWidget("serviceOrderServiceAppointmentListWidget", null, PageWidget.WidgetType.SERVICE_APPOINTMENT_LIST, "mobileServiceAppointmentListWidget_16", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("plans", "Plans",PageTabContext.TabType.SIMPLE,  true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("plans", null, null)
                .addWidget("serviceOrderPlansCostWidget", "Plans Cost", PageWidget.WidgetType.PLANS_COST_WIDGET, "mobilePlansCostWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("plansWidgetGroup", "Plans Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_16", 0, 2, null, getMobilePlansWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("actuals", "Actuals",PageTabContext.TabType.SIMPLE,  true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("actuals", null, null)
                .addWidget("serviceOrderActualsCostWidget", "Actuals Cost", PageWidget.WidgetType.ACTUALS_COST, "mobileActualsCostWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("actualsWidgetGroup", "Actuals Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_16", 0, 2, null, getMobileActualsWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addTab("history", "History",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblemobileactivity_16", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .layoutDone();
    }
    private static JSONObject getMobilePlansWidgetGroup() throws Exception {

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("plannedItems", "Items", "")
                .addWidget("serviceOrderPlannedItemsListWidget", "Items", PageWidget.WidgetType.PLANNED_ITEMS_LIST,"flexiblemobileplanneditems_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("plannedTools", "Tools", "")
                .addWidget("serviceOrderPlannedToolsListWidget", "Tools", PageWidget.WidgetType.PLANNED_TOOLS_LIST,"flexiblemobileplannedtools_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("plannedServices", "Services", "")
                .addWidget("serviceOrderPlannedServicesListWidget", "Services", PageWidget.WidgetType.PLANNED_SERVICES_LIST,"flexiblemobileplannedservices_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getMobileActualsWidgetGroup() throws Exception {

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("actualItems", "Items", "")
                .addWidget("serviceOrderActualItemsListWidget", "Items", PageWidget.WidgetType.ACTUAL_ITEMS_LIST,"flexiblemobileactualitems_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("actualTools", "Tools", "")
                .addWidget("serviceOrderActualToolsListWidget", "Tools", PageWidget.WidgetType.ACTUAL_TOOLS_LIST,"flexiblemobileactualtools_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("actualServices", "Services", "")
                .addWidget("serviceOrderActualServicesListWidget", "Services", PageWidget.WidgetType.ACTUAL_SERVICES_LIST,"flexiblemobileactualservices_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
    private static JSONObject getSummaryWidgetDetails(ApplicationContext app,String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField categoryField = moduleBean.getField("category", moduleName);
        FacilioField statusField = moduleBean.getField("status", moduleName);
        FacilioField priorityField = moduleBean.getField("priority", moduleName);

        FacilioField sourceTypeField = moduleBean.getField("sourceType", moduleName);
        FacilioField maintenanceTypeField = moduleBean.getField("maintenancetype", moduleName);
        FacilioField acsaField = moduleBean.getField("autoCreateSa", moduleName);

        FacilioField descriptionField = moduleBean.getField("description", moduleName);

        FacilioField siteField = moduleBean.getField("site", moduleName);
        FacilioField locationField = moduleBean.getField("location", moduleName);
        FacilioField spaceField = moduleBean.getField("space", moduleName);
        FacilioField assetField = moduleBean.getField("asset", moduleName);

        FacilioField fieldAgentField = moduleBean.getField("fieldAgent", moduleName);
        FacilioField vendorField = moduleBean.getField("vendor", moduleName);
        FacilioField clientField = moduleBean.getField("client", moduleName);

        FacilioField prefStartTimeField = moduleBean.getField("preferredStartTime", moduleName);
        FacilioField prefEndTimeField = moduleBean.getField("preferredEndTime", moduleName);

        FacilioField responseDueDurationField = moduleBean.getField("responseDueDate", moduleName);
        FacilioField resolutionDueDurationField = moduleBean.getField("resolutionDueDate", moduleName);
        FacilioField responseDueDateField = moduleBean.getField("responseDueDate", moduleName);
        FacilioField resolutionDueDateField = moduleBean.getField("resolutionDueDate", moduleName);
        FacilioField responseDueStatusField = moduleBean.getField("status", moduleName);
        FacilioField resolutionDueStatusField = moduleBean.getField("status", moduleName);

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, categoryField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, statusField, 1 , 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, priorityField, 1 , 4, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, sourceTypeField, 2 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, maintenanceTypeField, 2 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, acsaField, 2 , 3, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, descriptionField, 3 , 1, 4);

        addSummaryFieldInWidgetGroup(widgetGroup, siteField, 4 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, locationField, 4 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, spaceField, 4 , 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, assetField, 4 , 4, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, fieldAgentField, 5 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, vendorField, 5, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, clientField, 5, 3, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, prefStartTimeField, 6 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, prefEndTimeField, 6, 2, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, responseDueDurationField, 7 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, resolutionDueDurationField, 7, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, responseDueDateField, 7 , 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, resolutionDueDateField, 7 , 4, 1);

        addSummaryFieldInWidgetGroup(widgetGroup, responseDueStatusField, 8 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, resolutionDueStatusField, 8, 2, 1);


        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedByField, 9, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTimeField, 9, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedByField,9, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTimeField, 9, 4, 1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("Module Details");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if(field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if(widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            }
            else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0,commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0,attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }
}
