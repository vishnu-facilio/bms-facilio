package com.facilio.fsm.signup;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class TimeSheetModule extends BaseModuleConfig {

    public TimeSheetModule() throws Exception {
        setModuleName(FacilioConstants.TimeSheet.TIME_SHEET);
    }

    @Override
    public void addData() throws Exception {

        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule timeSheetModule = new FacilioModule(FacilioConstants.TimeSheet.TIME_SHEET,"Time Sheet","TIME_SHEET", FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> timeSheetFields = new ArrayList<>();

        DateField startTime = new DateField(timeSheetModule,"actualStartTime","Actual Start Time", FacilioField.FieldDisplayType.DATETIME,"ACTUAL_START_TIME", FieldType.DATE_TIME,true,false,true,false);
        timeSheetFields.add(startTime);

        DateField endTime = new DateField(timeSheetModule,"actualEndTime","Actual End Time", FacilioField.FieldDisplayType.DATETIME,"ACTUAL_END_TIME",FieldType.DATE_TIME,true,false,true,false);
        timeSheetFields.add(endTime);

        LookupField fieldAgent = new LookupField(timeSheetModule,"fieldAgent","Field Agent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"PEOPLE_ID",FieldType.LOOKUP,true,false,true,true,"Field Agent", Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE));
        timeSheetFields.add(fieldAgent);

        FacilioField actualDuration = FieldFactory.getDefaultField("actualDuration","Actual Duration","ACTUAL_DURATION", FieldType.NUMBER);
        actualDuration.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        timeSheetFields.add(actualDuration);

        LookupField actualStartLocation = new LookupField(timeSheetModule,"actualStartLocation","Actual Start Location", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"ACTUAL_START_LOCATION",FieldType.LOOKUP,true,false,true,false,"Start Location", Constants.getModBean().getModule(FacilioConstants.ContextNames.LOCATION));
        timeSheetFields.add(actualStartLocation);

        LookupField actualEndLocation = new LookupField(timeSheetModule,"actualEndLocation","Actual End Location", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"ACTUAL_END_LOCATION",FieldType.LOOKUP,true,false,true,false,"End Location", Constants.getModBean().getModule(FacilioConstants.ContextNames.LOCATION));
        timeSheetFields.add(actualEndLocation);

        LookupField serviceAppointment = new LookupField(timeSheetModule,"serviceAppointment","Service Appointment", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_APPOINTMENT_ID",FieldType.LOOKUP,true,false,true,false,"Service Appointment", Constants.getModBean().getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
        timeSheetFields.add(serviceAppointment);

        LookupField moduleStateField = FieldFactory.getDefaultField("moduleState", "Status", "MODULE_STATE", FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        timeSheetFields.add(moduleStateField);

        NumberField stateFlowIdField = FieldFactory.getDefaultField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        timeSheetFields.add(stateFlowIdField);

        LookupField approvalStateField = FieldFactory.getDefaultField("approvalStatus", "Approval Status", "APPROVAL_STATE", FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(moduleBean.getModule("ticketstatus"));
        timeSheetFields.add(approvalStateField);

        NumberField approvalFlowIdField = FieldFactory.getDefaultField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", FieldType.NUMBER);
        approvalFlowIdField.setDefault(true);
        timeSheetFields.add(approvalFlowIdField);

        timeSheetModule.setFields(timeSheetFields);
        modules.add(timeSheetModule);


        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        addTimeSheetTaskModule();
        addTimeSheetTasksField();
        addActivityModuleForTimeSheet();
        SignupUtil.addNotesAndAttachmentModule(timeSheetModule);

    }
    private void addTimeSheetTaskModule() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule serviceTaskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule timeSheetModule = modBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        FacilioModule timeSheetTaskModule = new FacilioModule(FacilioConstants.TimeSheet.TIME_SHEET_TASK,"Time Sheet Tasks","TIME_SHEET_TASK_REL",FacilioModule.ModuleType.SUB_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField serviceTaskField = new LookupField(timeSheetTaskModule,"right","Service Tasks",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_TASK_ID",FieldType.LOOKUP,true,false,true,false,"Service Tasks",serviceTaskModule);
        fields.add(serviceTaskField);
        LookupField timeSheetField = new LookupField(timeSheetTaskModule,"left","Time Sheet", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"TIME_SHEET_ID",FieldType.LOOKUP,true,false,true,true,"Time Sheet",timeSheetModule);
        fields.add(timeSheetField);
        timeSheetTaskModule.setFields(fields);
        modules.add(timeSheetTaskModule);
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();
    }
    private void addTimeSheetTasksField() throws Exception{
        ModuleBean modBean = Constants.getModBean();
        List<FacilioField>fields = new ArrayList<>();
        MultiLookupField multiLookupTasksField = FieldFactory.getDefaultField("serviceTasks", "Service Tasks", null, FieldType.MULTI_LOOKUP);
        multiLookupTasksField.setDisplayType(FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE);
        multiLookupTasksField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
        multiLookupTasksField.setLookupModule( modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK));
        multiLookupTasksField.setRelModule(modBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET_TASK));
        multiLookupTasksField.setRelModuleId(modBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET_TASK).getModuleId());
        fields.add(multiLookupTasksField);
        FacilioChain chain = TransactionChainFactory.getAddFieldsChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.TimeSheet.TIME_SHEET);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, fields);
        chain.execute();
    }
    public void addActivityModuleForTimeSheet() throws Exception {
        // TODO Auto-generated method stub

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule timeSheet = modBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);

        FacilioModule module = new FacilioModule(FacilioConstants.TimeSheet.TIME_SHEET_ACTIVITY,
                "Time Sheet Activity",
                "Time_Sheet_Activity",
                FacilioModule.ModuleType.ACTIVITY
        );


        List<FacilioField> fields = new ArrayList<>();

        NumberField parentId = FieldFactory.getDefaultField("parentId", "Parent", "PARENT_ID", FieldType.NUMBER);
        fields.add(parentId);

        FacilioField timefield = FieldFactory.getDefaultField("ttime", "Timestamp", "TTIME", FieldType.DATE_TIME);

        fields.add(timefield);

        NumberField type = FieldFactory.getDefaultField("type", "Type", "ACTIVITY_TYPE", FieldType.NUMBER);
        fields.add(type);

        LookupField doneBy = FieldFactory.getDefaultField("doneBy", "Done By", "DONE_BY_ID", FieldType.LOOKUP);
        doneBy.setSpecialType("users");
        fields.add(doneBy);

        FacilioField info = FieldFactory.getDefaultField("infoJsonStr", "Info", "INFO", FieldType.STRING);

        fields.add(info);


        module.setFields(fields);

        FacilioChain addModuleChain1 = TransactionChainFactory.addSystemModuleChain();
        addModuleChain1.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain1.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain1.execute();

        modBean.addSubModule(timeSheet.getModuleId(), module.getModuleId());
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);

        List<String> appNames = new ArrayList<>();
        appNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);

        Map<String, List<PagesContext>> appNameVsPage = new HashMap<>();
        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName, createTimesheetPage(app, module, false, true));
        }
        return appNameVsPage;

    }
    private static List<PagesContext> createTimesheetPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.TimeSheet.TIME_SHEET_ACTIVITY);

        return new ModulePages()
                .addPage("timeSheet", "Time Sheet", "", null, isTemplate, isDefault, false)
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
                .addWidget("timesheetbulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 4, null, RelatedListWidgetUtil.addAllRelatedModuleToWidget(FacilioConstants.TimeSheet.TIME_SHEET))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("timesheethistory", "HISTORY", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_60", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .pageDone().getCustomPages();


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
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("activityModuleName", FacilioConstants.TimeSheet.TIME_SHEET_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("activityModuleName", FacilioConstants.TimeSheet.TIME_SHEET_ATTACHMENTS);

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
