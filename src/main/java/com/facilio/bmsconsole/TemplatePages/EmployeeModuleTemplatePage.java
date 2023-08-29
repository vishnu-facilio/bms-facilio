package com.facilio.bmsconsole.TemplatePages;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.EMPLOYEE;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {


        return new PagesContext(null, null, "", null, true, false, false)
                .addWebTab("employeesummary", "SUMMARY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("employeesummaryfields", null, null)
                .addWidget("employeesummaryfieldswidget", "Employee Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfiledswidget_22", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.ContextNames.EMPLOYEE))
                .widgetDone()
                .sectionDone()
                .addSection("employeelocationstatus", null, null)
                .addWidget("employeelastknowlocationwidget", "Last Known Location", PageWidget.WidgetType.LAST_KNOW_LOCATION, "weblastknowlocation_22_6", 0, 0, null, null)
                .widgetDone()
                .addWidget("employeecurrentstatuswidget", "Employee Current Status", PageWidget.WidgetType.CURRENT_STATUS, "webcurrentstatus_22_6", 6, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("employeeskill", "SKILL", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("employeeskill", null, null)
                .addWidget("employeeskillwidget", "Skill", PageWidget.WidgetType.SKILL, "flexiblewebskill_48", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("employeeworkschedule", "WORK SCHEDULE", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("employeeworkschedule", null, null)
                .addWidget("employeeworkschedulewidget", "Work Schedule", PageWidget.WidgetType.WORK_SCHEDULE, "flexiblewebworkschedule_50", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("employeerelated", "RELATED", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("employeerelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("employeebulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("employeerelatedlist", "Related List", "List of related records across modules")
                .addWidget("employeebulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.addAllRelatedModuleToWidget(getModuleName()))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("employeehistory", "HISTORY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_50", 0, 0, null, getHistoryWidgetGroup(false))
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
        FacilioField phoneField = moduleBean.getField("phone", moduleName);
        FacilioField emailField = moduleBean.getField("email", moduleName);
        FacilioField roleField = moduleBean.getField("roleId", moduleName);
        FacilioField dispatchableField = moduleBean.getField("dispatchable", moduleName);
        FacilioField trackGeoLocationField = moduleBean.getField("trackGeoLocation", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, phoneField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, emailField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, roleField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, dispatchableField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, trackGeoLocationField, 2, 2, 1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);

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
        commentWidgetParam.put("notesModuleName", FacilioConstants.ContextNames.EMPLOYEE_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.ContextNames.EMPLOYEE_ATTACHMENTS);

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

    private static JSONObject getHistoryWidgetGroup(boolean isMobile) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.EMPLOYEE_ACTIVITY);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("fieldUpdate", "Field Update", "")
                .addWidget("fieldUpdatewidget", "Field Update", PageWidget.WidgetType.ACTIVITY, isMobile ? "flexiblemobilecomment_8" : "flexiblewebactivity_50", 0, 0, historyWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("location", "Location", "")
                .addWidget("locationwidget", "Location", PageWidget.WidgetType.EMPLOYEE_LOCATION, isMobile ? "flexiblemobileattachment_8" : "flexiblewebactivity_50", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}
