package com.facilio.bmsconsole.TemplatePages;

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

public class TimeSheetModuleTemplatePage implements TemplatePageFactory {
    @Override
    public String getModuleName() {
        return FacilioConstants.TimeSheet.TIME_SHEET;
    }

    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.TimeSheet.TIME_SHEET_ACTIVITY);

        return new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("timeSheetSummary", "Summary",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("timeSheetSummaryFields", null, null)
                .addWidget("timeSheetSummaryFieldsWidget", "Time Sheet Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.TimeSheet.TIME_SHEET))
                .widgetDone()
                .sectionDone()
                .addSection("timeSheetTaskList",null,null)
                .addWidget("timeSheetTaskListWidget","Tasks",PageWidget.WidgetType.TIMESHEET_TASKS,"timesheettasks_23_12",0,0,null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("timeSheetHistory", "History",PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_50", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                ;


    }

    private static JSONObject getSummaryWidgetDetails(ApplicationContext app, String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField fieldAgent = moduleBean.getField("fieldAgent", moduleName);
        FacilioField serviceAppointment = moduleBean.getField("serviceAppointment", moduleName);
        FacilioField startTime=moduleBean.getField("startTime",moduleName);
        FacilioField endTime=moduleBean.getField("endTime",moduleName);
        FacilioField duration=moduleBean.getField("duration",moduleName);


        FacilioField sysCreatedBy = moduleBean.getField("sysCreatedBy", moduleName);
        FacilioField sysCreatedTime = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedBy = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTime = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup gerneralInfoWidgetGroup = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(gerneralInfoWidgetGroup, fieldAgent, 1, 1, 1);
        addSummaryFieldInWidgetGroup(gerneralInfoWidgetGroup, serviceAppointment, 1, 2, 1);
        addSummaryFieldInWidgetGroup(gerneralInfoWidgetGroup, startTime, 1, 3, 1);
        addSummaryFieldInWidgetGroup(gerneralInfoWidgetGroup, endTime, 1, 4, 1);
        addSummaryFieldInWidgetGroup(gerneralInfoWidgetGroup, duration, 2, 1, 1);

        SummaryWidgetGroup systemDetailsWidgetGroup = new SummaryWidgetGroup();


        addSummaryFieldInWidgetGroup(systemDetailsWidgetGroup, sysCreatedBy, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemDetailsWidgetGroup, sysCreatedTime, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemDetailsWidgetGroup, sysModifiedBy, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemDetailsWidgetGroup, sysModifiedTime, 1, 4, 1);

        gerneralInfoWidgetGroup.setName("moduleDetails");
        gerneralInfoWidgetGroup.setDisplayName("General Information");
        gerneralInfoWidgetGroup.setColumns(4);

        systemDetailsWidgetGroup.setName("systemDetails");
        systemDetailsWidgetGroup.setDisplayName("System Details");
        systemDetailsWidgetGroup.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(gerneralInfoWidgetGroup);
        widgetGroupList.add(systemDetailsWidgetGroup);

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
        commentWidgetParam.put("notesModuleName", FacilioConstants.TimeSheet.TIME_SHEET_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.TimeSheet.TIME_SHEET_ATTACHMENTS);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_27", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }
}
