package com.facilio.bmsconsole.TemplatePages;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeSheetModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.TimeSheet.TIME_SHEET;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.TimeSheet.TIME_SHEET);

        return new PagesContext(null, null, "", null, true, false, false)
                .addWebTab("timesheetsummary", "SUMMARY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("timesheetsummaryfields", null, null)
                .addWidget("timesheetysummaryfieldswidget", "Time Sheet Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.TimeSheet.TIME_SHEET))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()


                .addWebTab("timesheetrelated", "RELATED", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("timesheetrelationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("timesheetbulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("timesheetrelatedlist", "Related List", "List of related records across modules")
                .addWidget("timesheetbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("timesheethistory", "HISTORY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                ;


    }

    private static JSONObject getSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField fieldAgent = moduleBean.getField("fieldAgent", moduleName);
        FacilioField moduleState = moduleBean.getField("moduleState", moduleName);
        FacilioField approvalStatus = moduleBean.getField("approvalStatus", moduleName);
        FacilioField serviceAppointment = moduleBean.getField("serviceAppointment", moduleName);
        FacilioField serviceTasks = moduleBean.getField("serviceTasks", moduleName);
        FacilioField actualStartTime = moduleBean.getField("actualStartTime", moduleName);
        FacilioField actualEndTime = moduleBean.getField("actualEndTime", moduleName);
        FacilioField actualDuration = moduleBean.getField("actualDuration", moduleName);
        FacilioField actualStartLocation = moduleBean.getField("actualStartLocation", moduleName);
        FacilioField actualEndLocation = moduleBean.getField("actualEndLocation", moduleName);
        FacilioField sysCreatedBy = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedBy = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup, fieldAgent, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, moduleState, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, approvalStatus, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, serviceAppointment, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, serviceTasks, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, actualStartTime, 2, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, actualEndTime, 2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, actualDuration, 2, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, actualStartLocation, 3, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, actualEndLocation, 3, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedBy, 3, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysCreatedTime, 3, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedBy, 4, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, sysModifiedTime, 4, 2, 1);

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
        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_27", 0, 0, null, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}
