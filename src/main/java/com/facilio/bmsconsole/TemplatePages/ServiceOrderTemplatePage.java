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
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_ACTIVITY);
        if(app.getDomainType() == AppDomain.AppDomainType.FACILIO.getIndex()) {
            return  new PagesContext(null, null,"", null, true, false, false)
                    .addWebTab("summary", "SUMMARY", true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null, null)
                    .addWidget("summaryFieldsWidget", "Summary Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(serviceOrderModule.getName()))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 4, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("appointments", "SERVICE APPOINTMENT", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("appointments", null, null)
                    .addWidget("appointments", "Appointments", PageWidget.WidgetType.SERVICE_ORDER_APPOINTMENTS, "appointments_50_12", 0, 0,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("appointment", "SERVICE APPOINTMENT", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("task", null, null)
                    .addWidget("tasklist", "Tasks", PageWidget.WidgetType.SERVICE_TASK_WIDGET, "webtasklist_50_12", 0, 0,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("plans", "PLANS", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("task", null, null)
                    .addWidget("tasklist", "Tasks", PageWidget.WidgetType.SERVICE_TASK_WIDGET, "webtasklist_50_12", 0, 0,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("actuals", "ACTUALS", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("task", null, null)
                    .addWidget("tasklist", "Tasks", PageWidget.WidgetType.SERVICE_TASK_WIDGET, "webtasklist_50_12", 0, 0,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("related", "RELATED", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relatedlist", null, null)
                    .addWidget("bulkRelatedList", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.addAllRelatedModuleToWidget(FacilioConstants.ContextNames.QUOTE))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("history", "HISTORY", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("activity", null, null)
                    .addWidget("activity", "Activity", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_50", 0, 0,  historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone();
        } else {
            return  new PagesContext(null, null,"", null, true, false, false)
                    .addWebTab("summary", "SUMMARY", true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("summaryfields", null, null)
                    .addWidget("summaryFieldsWidget", "Summary Widget", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(serviceOrderModule.getName()))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 4, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("task", "TASK", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("task", null, null)
                    .addWidget("tasklist", "Tasks", PageWidget.WidgetType.SERVICE_TASK_WIDGET, "webtasklist_50_12", 0, 0,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("appointment", "SERVICE APPOINTMENT", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("task", null, null)
                    .addWidget("tasklist", "Tasks", PageWidget.WidgetType.SERVICE_TASK_WIDGET, "webtasklist_50_12", 0, 0,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("plans", "PLANS", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("task", null, null)
                    .addWidget("tasklist", "Tasks", PageWidget.WidgetType.SERVICE_TASK_WIDGET, "webtasklist_50_12", 0, 0,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("actuals", "ACTUALS", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("task", null, null)
                    .addWidget("tasklist", "Tasks", PageWidget.WidgetType.SERVICE_TASK_WIDGET, "webtasklist_50_12", 0, 0,  null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("related", "RELATED", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relatedlist", null, null)
                    .addWidget("bulkRelatedList", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.addAllRelatedModuleToWidget(FacilioConstants.ContextNames.QUOTE))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .addWebTab("history", "HISTORY", true, null)
                    .addColumn( PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("activity", null, null)
                    .addWidget("activity", "Activity", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_50", 0, 0,  historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone();
        }
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField nameField = moduleBean.getField("name", moduleName);
        FacilioField categoryField = moduleBean.getField("category", moduleName);
        FacilioField statusField = moduleBean.getField("status", moduleName);
        FacilioField priorityField = moduleBean.getField("priority", moduleName);

        FacilioField sourceTypeField = moduleBean.getField("sourceType", moduleName);
        FacilioField maintenanceTypeField = moduleBean.getField("maintenancetype", moduleName);
//        FacilioField priorityField = moduleBean.getField("priority", moduleName);
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

        addSummaryFieldInWidgetGroup(widgetGroup, sourceTypeField, 2 , 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, maintenanceTypeField, 2 , 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, acsaField, 2 , 4, 1);

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
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
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
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_27", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_27", 0, 4, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();


        return FieldUtil.getAsJSON(widgetGroup);
    }
}
