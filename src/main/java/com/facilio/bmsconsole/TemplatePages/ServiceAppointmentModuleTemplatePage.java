package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
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

public class ServiceAppointmentModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT_ACTIVITY);

        return new PagesContext(null, null, "", null, true, false, false)
                .addWebTab("serviceandappointmentsummary", "SUMMARY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceandappointmentsummaryfields", null, null)
                .addWidget("serviceandappointmentsummaryfieldswidget", "Service Appointment Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceandappointmentservicetasks", "SERVICE TASKS", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceandappointmentservicetasks", null, null)
                .addWidget("serviceandappointmentservicetaskswidget", "Service Tasks", PageWidget.WidgetType.SERVICE_APPOINTMENT_SERVICE_TASKS, "serviceandappointmentservicetasks_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceandappointmentplans", "PLANS", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceandappointmentplans", null, null)
                .addWidget("serviceandappointmentplanswidget", "Plans", PageWidget.WidgetType.SERVICE_APPOINTMENT_PLANS, "serviceandappointmentplans_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceandappointmentactuals", "ACTUALS", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceandappointmentactuals", null, null)
                .addWidget("serviceandappointmentactualswidget", "Actuals", PageWidget.WidgetType.SERVICE_APPOINTMENT_ACTUALS, "serviceandappointmentactuals_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceandappointmenttimesheet", "TIME SHEET", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceandappointmenttimesheet", null, null)
                .addWidget("serviceandappointmenttimesheetwidget", "Time Sheet", PageWidget.WidgetType.SERVICE_APPOINTMENT_TIMESHEET, "serviceandappointmenttimesheet_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceandappointmenttrip", "TRIP", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceandappointmenttrip", null, null)
                .addWidget("serviceandappointmenttripwidget", "Trip", PageWidget.WidgetType.SERVICE_APPOINTMENT_TRIP, "serviceandappointmenttrip_50_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addWebTab("serviceandappointmentrelated", "RELATED", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("serviceandappointmentrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("serviceandappointmentbulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("serviceandappointmentrelatedlist", "Related List", "List of related records across modules")
                .addWidget("serviceandappointmentbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.addAllRelatedModuleToWidget(getModuleName()))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("serviceandappointmenthistory", "HISTORY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_60", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                ;


    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField moduleStateField = moduleBean.getField("status", moduleName);
        FacilioField priorityField = moduleBean.getField("priority", moduleName);
        FacilioField appointmentTypeField = moduleBean.getField("appointmentType", moduleName);
        FacilioField descriptionField = moduleBean.getField("description", moduleName);


        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup generalInformationwidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup siteInformationwidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup userDetailswidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup scheduleAndAppoinmentwidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup slaDetailswidgetGroup = new SummaryWidgetGroup();
        SummaryWidgetGroup systemDetailswidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, moduleStateField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, priorityField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, appointmentTypeField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(generalInformationwidgetGroup, descriptionField, 2, 1, 4);

        //Site Details

        FacilioField siteField = moduleBean.getField("site", moduleName);
        FacilioField locationField = moduleBean.getField("location", moduleName);
        FacilioField territoryField = moduleBean.getField("territory", moduleName);

        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, siteField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, locationField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(siteInformationwidgetGroup, territoryField, 1, 3, 1);

        // User Details

        FacilioField fieldAgentField = moduleBean.getField("fieldAgent", moduleName);

        addSummaryFieldInWidgetGroup(userDetailswidgetGroup, fieldAgentField, 1, 1, 1);

        // Schedule and Appointment

        FacilioField scheduledStartTimeField = moduleBean.getField("scheduledStartTime", moduleName);
        FacilioField scheduledEndTime = moduleBean.getField("scheduledEndTime", moduleName);


        addSummaryFieldInWidgetGroup(scheduleAndAppoinmentwidgetGroup, scheduledStartTimeField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(scheduleAndAppoinmentwidgetGroup, scheduledEndTime, 1, 2, 1);

        // SLA Details

        FacilioField actualStartTimeField = moduleBean.getField("actualStartTime", moduleName);
        FacilioField actualEndTimeField = moduleBean.getField("actualEndTime", moduleName);
        FacilioField responseDueTimeField = moduleBean.getField("responseDueTime", moduleName);
        FacilioField resolutionDueTimeField = moduleBean.getField("resolutionDueTime", moduleName);
        FacilioField responseDueStatusField = moduleBean.getField("responseDueStatus", moduleName);
        FacilioField resolutionDueStatusField = moduleBean.getField("resolutionDueStatus", moduleName);

        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, actualStartTimeField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, actualEndTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, responseDueTimeField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, resolutionDueTimeField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, responseDueStatusField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(slaDetailswidgetGroup, resolutionDueStatusField, 2, 2, 1);

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
        siteInformationwidgetGroup.setDisplayName("Site Details");
        siteInformationwidgetGroup.setColumns(4);

        userDetailswidgetGroup.setName("userDetails");
        userDetailswidgetGroup.setDisplayName("User Details");
        userDetailswidgetGroup.setColumns(4);

        scheduleAndAppoinmentwidgetGroup.setName("scheduleAndAppointment");
        scheduleAndAppoinmentwidgetGroup.setDisplayName("Schedule And Appointment");
        scheduleAndAppoinmentwidgetGroup.setColumns(4);

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
        widgetGroupList.add(scheduleAndAppoinmentwidgetGroup);
        widgetGroupList.add(slaDetailswidgetGroup);
        widgetGroupList.add(systemDetailswidgetGroup);


        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
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
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}
