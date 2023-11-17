package com.facilio.bmsconsole.TemplatePages;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceAppointmentModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY);

        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ATTACHMENTS);

        return new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("serviceAppointmentSummary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentSummaryFields", null, null)
                .addWidget("serviceAppointmentSummaryFieldsWidget", "Appointment Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addTab("serviceAppointmentServiceTasks", "Tasks",  PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentServiceTasks", null, null)
                .addWidget("serviceAppointmentServiceTasksWidget", "Tasks", PageWidget.WidgetType.SERVICE_APPOINTMENT_SERVICE_TASKS, "webServiceAppointmentServiceTasks_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointmentPlans", "Plans", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentPlans", null, null)
                .addWidget("serviceAppointmentPlansWidget", "Plans", PageWidget.WidgetType.SERVICE_APPOINTMENT_PLANS, "webServiceAppointmentPlans_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointmentActuals", "Actuals", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentActuals", null, null)
                .addWidget("serviceAppointmentActualsWidget", "Actuals", PageWidget.WidgetType.SERVICE_APPOINTMENT_ACTUALS, "webServiceAppointmentActuals_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointmentTimeSheet", "Time Sheet", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentTimeSheet", null, null)
                .addWidget("serviceAppointmentTimeSheetWidget", "Time Sheet", PageWidget.WidgetType.SERVICE_APPOINTMENT_TIMESHEET, "webServiceAppointmentTimeSheet_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointmentTrip", "Trip", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceAppointmentTrip", null, null)
                .addWidget("serviceAppointmentTripWidget", "Trip", PageWidget.WidgetType.SERVICE_APPOINTMENT_TRIP, "webServiceAppointmentTrip_10_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("serviceAppointmentHistory", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_10", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .addMobileLayout()

                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summary", null, null)
                .addWidget("summaryFieldsWidget", null, PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblemobilesummaryfieldswidget_14", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT))
                .widgetDone()
                .addWidget("serviceAppointmentResourceWidget", null, PageWidget.WidgetType.RESOURCE_WIDGET, "mobileFlexibleResourceWidget_6", 0, 15, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentLocationWidget", null, PageWidget.WidgetType.LOCATION_WIDGET, "mobileFlexibleLocationWidget_9", 0, 21, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentUserWidget", null, PageWidget.WidgetType.USER_WIDGET, "mobileFlexibleUserWidget_7", 0, 30, null, null)
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
                .addWidget("serviceAppointmentServiceTaskListWidget", null, PageWidget.WidgetType.SERVICE_TASK_LIST_WIDGET, "mobileServiceTaskListWidget_16", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("plans", "Plans",PageTabContext.TabType.SIMPLE,  true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("plans", null, null)
                .addWidget("serviceAppointmentPlansCostWidget", "Plans Cost", PageWidget.WidgetType.PLANS_COST_WIDGET, "mobilePlansCostWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentplansWidgetGroup", "Plans Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_16", 0, 2, null, getMobilePlansWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("actuals", "Actuals",PageTabContext.TabType.SIMPLE,  true, AccountUtil.FeatureLicense.INVENTORY)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("actuals", null, null)
                .addWidget("serviceAppointmentActualsCostWidget", "Actuals Cost", PageWidget.WidgetType.ACTUALS_COST, "mobileActualsCostWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentActualsWidgetGroup", "Actuals Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblemobilewidgetgroup_16", 0, 2, null, getMobileActualsWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("timeSheet", "Time Sheet",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("timeSheet", null, null)
                .addWidget("serviceAppointmentTimeSheetOverviewWidget", "Time Sheet Overview", PageWidget.WidgetType.TIME_SHEET_OVERVIEW, "mobileTimeSheetOverviewWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentTimeSheetListWidget", "Time Sheet List", PageWidget.WidgetType.TIME_SHEET_LIST, "mobileTimeSheetListWidget_16", 0, 2, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("trip", "Trip",PageTabContext.TabType.SIMPLE,  true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("trip", null, null)
                .addWidget("serviceAppointmentTripOverviewWidget", "Trip Overview", PageWidget.WidgetType.TRIP_OVERVIEW, "mobileTripOverviewWidget_2", 0, 0, null, null)
                .widgetDone()
                .addWidget("serviceAppointmentTripListWidget", "Trips", PageWidget.WidgetType.TRIP_LIST, "mobileTripListWidget_16", 0, 2, null, null)
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

                .layoutDone()
                ;


    }
    private static JSONObject getMobilePlansWidgetGroup() throws Exception {

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("plannedItems", "Items", "")
                .addWidget("plannedItemsListWidget", "Items", PageWidget.WidgetType.PLANNED_ITEMS_LIST,"flexiblemobileplanneditems_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("plannedTools", "Tools", "")
                .addWidget("plannedTollsListWidget", "Tools", PageWidget.WidgetType.PLANNED_TOOLS_LIST,"flexiblemobileplannedtools_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("plannedServices", "Services", "")
                .addWidget("plannedServicesListWidget", "Services", PageWidget.WidgetType.PLANNED_SERVICES_LIST,"flexiblemobileplannedservices_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getMobileActualsWidgetGroup() throws Exception {

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("actualItems", "Items", "")
                .addWidget("actualItemsListWidget", "Items", PageWidget.WidgetType.ACTUAL_ITEMS_LIST,"flexiblemobileactualitems_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("actualTools", "Tools", "")
                .addWidget("actualTollsListWidget", "Tools", PageWidget.WidgetType.ACTUAL_TOOLS_LIST,"flexiblemobileactualtools_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("actualServices", "Services", "")
                .addWidget("actualServicesListWidget", "Services", PageWidget.WidgetType.ACTUAL_SERVICES_LIST,"flexiblemobileactualservices_16", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static JSONObject getSummaryWidgetDetails(ApplicationContext app, String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("serviceOrder", moduleName);
        FacilioField descriptionField = moduleBean.getField("description", moduleName);
        FacilioField category = moduleBean.getField("category", moduleName);
        FacilioField priority = moduleBean.getField("priority", moduleName);
        FacilioField scheduledStartTimeField = moduleBean.getField("scheduledStartTime", moduleName);
        FacilioField scheduledEndTime = moduleBean.getField("scheduledEndTime", moduleName);
        FacilioField estimatedDuration = moduleBean.getField("estimatedDuration", moduleName);
        FacilioField actualStartTimeField = moduleBean.getField("actualStartTime", moduleName);
        FacilioField actualEndTimeField = moduleBean.getField("actualEndTime", moduleName);
        FacilioField actualDuration = moduleBean.getField("actualDuration", moduleName);


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup generalInformationwidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup siteInformationwidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup userDetailswidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup slaDetailswidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup systemDetailswidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, nameField, 1, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, descriptionField, 2, 1, 4);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, category, 3, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, priority, 3, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, scheduledStartTimeField, 3, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, scheduledEndTime, 3, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, estimatedDuration, 4, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, actualStartTimeField, 4, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, actualEndTimeField, 4, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, actualDuration, 4, 4, 1);

        //Site Details

        FacilioField siteField = moduleBean.getField("site", moduleName);
        FacilioField locationField = moduleBean.getField("location", moduleName);
        FacilioField space = moduleBean.getField("space", moduleName);
        FacilioField asset = moduleBean.getField("asset", moduleName);


        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, siteField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, locationField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, space, 1, 3, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, asset, 1, 4, 1);


        // User Details

        FacilioField fieldAgentField = moduleBean.getField("fieldAgent", moduleName);
        FacilioField vendor = moduleBean.getField("vendor", moduleName);
        FacilioField client = moduleBean.getField("client", moduleName);

        addSummaryFieldInWidgetGroup(userDetailswidgetGroup, fieldAgentField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(userDetailswidgetGroup, vendor, 1, 2, 1);
        addSummaryFieldInWidgetGroup(userDetailswidgetGroup, client, 1, 3, 1);

        // SLA Details

        FacilioField responseDueDuration = moduleBean.getField("responseDueDuration", moduleName);
        FacilioField resolutionDueDuration = moduleBean.getField("resolutionDueDuration", moduleName);
        FacilioField responseDueTimeField = moduleBean.getField("responseDueTime", moduleName);
        FacilioField resolutionDueTimeField = moduleBean.getField("resolutionDueTime", moduleName);
        FacilioField responseDueStatus=moduleBean.getField("responseDueStatus",moduleName);
        FacilioField resolutionDueStatus=moduleBean.getField("resolutionDueStatus",moduleName);

        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, responseDueDuration, 1, 1, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, resolutionDueDuration, 1, 2, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, responseDueTimeField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, resolutionDueTimeField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup,responseDueStatus,2,1,1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup,resolutionDueStatus,2,2,1);


        // System Details

        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemDetailswidgetGroup, sysModifiedTimeField, 1, 4, 1);


        generalInformationwidgetGroup.setName("moduleDetails");
        generalInformationwidgetGroup.setDisplayName("General Information");
        generalInformationwidgetGroup.setColumns(4);

        siteInformationwidgetGroup.setName("siteDetails");
        siteInformationwidgetGroup.setDisplayName("Site Information");
        siteInformationwidgetGroup.setColumns(4);

        userDetailswidgetGroup.setName("userDetails");
        userDetailswidgetGroup.setDisplayName("User Details");
        userDetailswidgetGroup.setColumns(4);

        slaDetailswidgetGroup.setName("slaDetails");
        slaDetailswidgetGroup.setDisplayName("SLA Details");
        slaDetailswidgetGroup.setColumns(4);

        systemDetailswidgetGroup.setName("systemDetails");
        systemDetailswidgetGroup.setDisplayName("System Details");
        systemDetailswidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(generalInformationwidgetGroup);
        widgetGroupList.add(siteInformationwidgetGroup);
        widgetGroupList.add(userDetailswidgetGroup);
        widgetGroupList.add(slaDetailswidgetGroup);
        widgetGroupList.add(systemDetailswidgetGroup);


        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }

    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if (field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);

            if (widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            } else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }

    private static JSONObject getSummaryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("notesModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Attachments", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}
