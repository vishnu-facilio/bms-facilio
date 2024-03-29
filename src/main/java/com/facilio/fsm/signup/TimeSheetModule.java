package com.facilio.fsm.signup;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class TimeSheetModule extends BaseModuleConfig {
    public static List<String> timeSheetSupportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.FSM_APP);
    public TimeSheetModule() throws Exception {
        setModuleName(FacilioConstants.TimeSheet.TIME_SHEET);
    }

    @Override
    public void addData() throws Exception {

        ModuleBean moduleBean = Constants.getModBean();
        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule timeSheetModule = new FacilioModule(FacilioConstants.TimeSheet.TIME_SHEET,"Time Sheet","TIME_SHEET", FacilioModule.ModuleType.BASE_ENTITY,true);
        timeSheetModule.setDescription("Log and manage field agents' work hours with precision, ensuring accurate tracking and streamlined time management for every appointment as part of field service.");

        List<FacilioField> timeSheetFields = new ArrayList<>();

        NumberField localId = new NumberField(timeSheetModule,"localId","Id", FacilioField.FieldDisplayType.NUMBER,"LOCAL_ID",FieldType.NUMBER,false,false,true,false);
        timeSheetFields.add(localId);
        ModuleLocalIdUtil.insertModuleLocalId(FacilioConstants.TimeSheet.TIME_SHEET);

        timeSheetFields.add(new StringField(timeSheetModule,"code","Code",FacilioField.FieldDisplayType.TEXTBOX,"CODE", FieldType.STRING,true,false,true,true));

        DateField startTime = new DateField(timeSheetModule,"startTime","Start Time", FacilioField.FieldDisplayType.DATETIME,"START_TIME", FieldType.DATE_TIME,true,false,true,false);
        timeSheetFields.add(startTime);

        DateField endTime = new DateField(timeSheetModule,"endTime","End Time", FacilioField.FieldDisplayType.DATETIME,"END_TIME",FieldType.DATE_TIME,true,false,true,false);
        timeSheetFields.add(endTime);

        FacilioField actualDuration = FieldFactory.getDefaultField("duration","Duration","DURATION", FieldType.NUMBER);
        actualDuration.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        timeSheetFields.add(actualDuration);

        LookupField fieldAgent = new LookupField(timeSheetModule,"fieldAgent","Field Agent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"PEOPLE_ID",FieldType.LOOKUP,true,false,true,false,"Field Agent", Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE));
        timeSheetFields.add(fieldAgent);

        LookupField serviceAppointment = new LookupField(timeSheetModule,"serviceAppointment","Appointment", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_APPOINTMENT_ID",FieldType.LOOKUP,true,false,true,false,"Appointments", Constants.getModBean().getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
        timeSheetFields.add(serviceAppointment);

        LookupField serviceOrder = new LookupField(timeSheetModule,"serviceOrder","Work Order", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_ORDER_ID",FieldType.LOOKUP,true,false,true,false,"Work Orders", Constants.getModBean().getModule(FacilioConstants.ContextNames.SERVICE_ORDER));
        timeSheetFields.add(serviceOrder);

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

        LookupField status = FieldFactory.getDefaultField("status","Time Sheet Status","STATUS",FieldType.LOOKUP);
        status.setRequired(true);
        status.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        status.setLookupModule(moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET_STATUS));
        timeSheetFields.add(status);

        SystemEnumField type = (SystemEnumField) FieldFactory.getDefaultField("type", "Type", "TYPE", FieldType.SYSTEM_ENUM);
        type.setEnumName("TimeSheetType");
        timeSheetFields.add(type);

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
        addSystemButtons();

    }
    private void addTimeSheetTaskModule() throws Exception {
        List<FacilioModule> modules = new ArrayList<>();
        ModuleBean modBean = Constants.getModBean();
        FacilioModule serviceTaskModule = modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK);
        FacilioModule timeSheetModule = modBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        FacilioModule timeSheetTaskModule = new FacilioModule(FacilioConstants.TimeSheet.TIME_SHEET_TASK,"Time Sheet Tasks","TIME_SHEET_TASK_REL",FacilioModule.ModuleType.SUB_ENTITY,true);
        List<FacilioField> fields = new ArrayList<>();
        LookupField serviceTaskField = new LookupField(timeSheetTaskModule,"right","Task",FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_TASK_ID",FieldType.LOOKUP,true,false,true,false,"Service Tasks",serviceTaskModule);
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
        MultiLookupField multiLookupTasksField = FieldFactory.getDefaultField("serviceTasks", "Tasks", null, FieldType.MULTI_LOOKUP);
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
        appNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);

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
                .addPage("timeSheet", "Default Time Sheet Page", "", null, isTemplate, isDefault, false)
                .addWebLayout()
                .addTab("timeSheetSummary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("timeSheetSummaryFields", null, null)
                .addWidget("timeSheetSummaryFieldsWidget", "Time Sheet Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.TimeSheet.TIME_SHEET))
                .widgetDone()
                .sectionDone()
                .addSection("timeSheetTaskList",null,null)
                .addWidget("timeSheetTaskListWidget","Tasks",PageWidget.WidgetType.TIMESHEET_TASKS,"webTimeSheetTasks_5_12",0,0,null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("timeSheetHistory", "History",PageTabContext.TabType.SIMPLE,  true, null)
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
                .addWidget("summaryFieldsWidget", null, PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblemobilesummaryfieldswidget_14", 0, 0, null, getSummaryWidgetDetails(app,FacilioConstants.TimeSheet.TIME_SHEET))
                .widgetDone()
                .addWidget("timeSheetServiceAppointmentCard", null, PageWidget.WidgetType.SERVICE_APPOINTMENT_CARD, "mobileFlexibleTimeSheetServiceAppointmentCard_2", 0, 15, null, null)
                .widgetDone()
                .addWidget("timeSheetTasks", null, PageWidget.WidgetType.SERVICE_TASK_LIST_WIDGET, "mobileFlexibleTimeSheetServiceTaskList_16", 0, 17, null, null)
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
                .pageDone().getCustomPages();


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
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 0, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();

        return FieldUtil.getAsJSON(widgetGroup);
    }

    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule timeSheetModule = modBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        FacilioForm timeSheetForm =new FacilioForm();
        timeSheetForm.setDisplayName("Standard");
        timeSheetForm.setName("default_timeSheet_web");
        timeSheetForm.setModule(timeSheetModule);
        timeSheetForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        timeSheetForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        List<FormField> generalInformationFields = new ArrayList<>();

        generalInformationFields.add(new FormField("fieldAgent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Field Agent", FormField.Required.REQUIRED,1,2));
        generalInformationFields.add(new FormField("serviceAppointment", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Appointment",FormField.Required.REQUIRED,2,2));
        generalInformationFields.add(new FormField("startTime", FacilioField.FieldDisplayType.DATETIME, "Start Time", FormField.Required.REQUIRED, 3, 2));
        generalInformationFields.add(new FormField("endTime", FacilioField.FieldDisplayType.DATETIME, "End Time", FormField.Required.REQUIRED, 4, 2));

        FormSection generalSection = new FormSection("General Information", 1, generalInformationFields, true);
        generalSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> serviceTasksFields = new ArrayList<>();
        serviceTasksFields.add(new FormField("serviceTasks", FacilioField.FieldDisplayType.MULTI_LOOKUP_SIMPLE, "Tasks", FormField.Required.REQUIRED, 1, 1));
        FormSection serviceTaskSection = new FormSection("Task Details", 2, serviceTasksFields, true);

        serviceTaskSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> webTimeSheetFormSection = new ArrayList<>();
        webTimeSheetFormSection.add(generalSection);
        webTimeSheetFormSection.add(serviceTaskSection);

        timeSheetForm.setSections(webTimeSheetFormSection);
        timeSheetForm.setIsSystemForm(true);
        timeSheetForm.setType(FacilioForm.Type.FORM);

        List<FormRuleContext> formRules = new ArrayList<>();
        formRules.add(addTasksFilterRule());
        formRules.add(addEditFieldDisableRule());
        timeSheetForm.setDefaultFormRules(formRules);

        List<FacilioForm> timeSheetModuleForms = new ArrayList<>();
        timeSheetModuleForms.add(timeSheetForm);
        return timeSheetModuleForms;
    }

    private FormRuleContext addEditFieldDisableRule() {

        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Disabling field edit");
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());
        singleRule.setExecuteType(FormRuleContext.ExecuteType.EDIT.getIntVal());
        singleRule.setTriggerFields(new ArrayList<>());

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext filterAction = new FormRuleActionContext();
        filterAction.setActionType(FormActionType.DISABLE_FIELD.getVal());

        List<FormRuleActionFieldsContext> actionFields = new ArrayList<>();
        FormRuleActionFieldsContext workOrderField = new FormRuleActionFieldsContext();
        workOrderField.setFormFieldName("Appointment");
        actionFields.add(workOrderField);
        FormRuleActionFieldsContext fieldAgentField = new FormRuleActionFieldsContext();
        fieldAgentField.setFormFieldName("Field Agent");
        actionFields.add(fieldAgentField);
        FormRuleActionFieldsContext tasksField = new FormRuleActionFieldsContext();
        tasksField.setFormFieldName("Tasks");
        actionFields.add(tasksField);

        filterAction.setFormRuleActionFieldsContext(actionFields);

        actions.add(filterAction);

        singleRule.setActions(actions);
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return singleRule;
    }

    private FormRuleContext addTasksFilterRule() {

        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Tasks Filter Rule");
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_FORM.getIntVal());

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Appointment");
        singleRule.setTriggerFields(Collections.singletonList(triggerField));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext filterAction = new FormRuleActionContext();
        filterAction.setActionType(FormActionType.APPLY_FILTER.getVal());

        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();

        actionField.setFormFieldName("Tasks");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("SERVICE_TASK.SERVICE_APPOINTMENT","serviceAppointment", "${timeSheet.serviceAppointment.id}", NumberOperators.EQUALS));

        actionField.setCriteria(criteria);

        filterAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        actions.add(filterAction);

        singleRule.setActions(actions);
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return singleRule;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> timeSheetViews = new ArrayList<FacilioView>();
        timeSheetViews.add(getHiddenAllTimeSheetViews().setOrder(order++));
        timeSheetViews.add(getAllTimeSheetViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "default");
        groupDetails.put("displayName", "Default");
        groupDetails.put("moduleName", FacilioConstants.TimeSheet.TIME_SHEET);
        groupDetails.put("views", timeSheetViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getHiddenAllTimeSheetViews() throws Exception {
        FacilioModule timeSheetModule = Constants.getModBean().getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        FacilioView allView = new FacilioView();
        allView.setName("hidden-all");
        allView.setDisplayName("All Time Sheets");
        allView.setModuleName(FacilioConstants.TimeSheet.TIME_SHEET);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(TimeSheetModule.timeSheetSupportedApps);
        allView.setHidden(true);

        List<ViewField> timeSheetViewFields = new ArrayList<>();

        timeSheetViewFields.add(new ViewField("code","Code"));
        timeSheetViewFields.add(new ViewField("fieldAgent","Field Agent"));
        timeSheetViewFields.add(new ViewField("startTime","Start Time"));
        timeSheetViewFields.add(new ViewField("endTime","End Time"));
        timeSheetViewFields.add(new ViewField("serviceTasks","Tasks"));
        timeSheetViewFields.add(new ViewField("duration","Duration"));

        allView.setFields(timeSheetViewFields);

        return allView;
    }

    private FacilioView getAllTimeSheetViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("sysCreatedTime", "SYS_CREATED_TIME", FieldType.DATE_TIME), false));

        FacilioView allView = new FacilioView();
        allView.setName("alltimesheets");
        allView.setDisplayName("All Time Sheets");
        allView.setModuleName(FacilioConstants.TimeSheet.TIME_SHEET);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(TimeSheetModule.timeSheetSupportedApps);

        List<ViewField> timeSheetViewFields = new ArrayList<>();

        timeSheetViewFields.add(new ViewField("code","Code"));
        timeSheetViewFields.add(new ViewField("fieldAgent","Field Agent"));
        timeSheetViewFields.add(new ViewField("startTime","Start Time"));
        timeSheetViewFields.add(new ViewField("endTime","End Time"));
        timeSheetViewFields.add(new ViewField("serviceAppointment","Appointment"));
        timeSheetViewFields.add(new ViewField("serviceTasks","Tasks"));

        allView.setFields(timeSheetViewFields);

        return allView;
    }

    private void addSystemButtons() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.TimeSheet.TIME_SHEET);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SystemButtonRuleContext stopTimeSheet = new SystemButtonRuleContext();
        stopTimeSheet.setName("Stop Time Sheet");
        stopTimeSheet.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        stopTimeSheet.setIdentifier(FacilioConstants.TimeSheet.STOP_TIME_SHEET);
        stopTimeSheet.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        stopTimeSheet.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        stopTimeSheet.setPermissionRequired(true);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.ENDTIME),CommonOperators.IS_EMPTY));
        stopTimeSheet.setCriteria(criteria);
        SystemButtonApi.addSystemButton(FacilioConstants.TimeSheet.TIME_SHEET,stopTimeSheet);

        SystemButtonRuleContext stopTimeSheetButton = new SystemButtonRuleContext();
        stopTimeSheetButton.setName("Stop Time Sheet");
        stopTimeSheetButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        stopTimeSheetButton.setIdentifier(FacilioConstants.TimeSheet.STOP_TIME_SHEET);
        stopTimeSheetButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        stopTimeSheetButton.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        stopTimeSheetButton.setPermissionRequired(true);
        Criteria timeCriteria = new Criteria();
        timeCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.ENDTIME),CommonOperators.IS_EMPTY));
        stopTimeSheetButton.setCriteria(timeCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.TimeSheet.TIME_SHEET,stopTimeSheetButton);

        SystemButtonRuleContext create = new SystemButtonRuleContext();
        create.setName("Create Time Sheet");
        create.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        create.setIdentifier(FacilioConstants.ContextNames.CREATE);
        create.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        create.setPermission("CREATE");
        create.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.TimeSheet.TIME_SHEET,create);

        SystemButtonRuleContext edit = new SystemButtonRuleContext();
        edit.setName("Edit");
        edit.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        edit.setIdentifier("edit_list");
        edit.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        edit.setPermission("UPDATE");
        edit.setPermissionRequired(true);
        edit.setCriteria(getEditCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.TimeSheet.TIME_SHEET,edit);

        SystemButtonRuleContext summaryEditButton = new SystemButtonRuleContext();
        summaryEditButton.setName("Edit");
        summaryEditButton.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        summaryEditButton.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        summaryEditButton.setIdentifier("edit_summary");
        summaryEditButton.setPermissionRequired(true);
        summaryEditButton.setPermission("UPDATE");
        summaryEditButton.setCriteria(getEditCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.TimeSheet.TIME_SHEET,summaryEditButton);

        SystemButtonRuleContext listDeleteButton = new SystemButtonRuleContext();
        listDeleteButton.setName("Delete");
        listDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        listDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        listDeleteButton.setIdentifier("delete_list");
        listDeleteButton.setPermissionRequired(true);
        listDeleteButton.setPermission("DELETE");
        listDeleteButton.setCriteria(getDeleteCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.TimeSheet.TIME_SHEET,listDeleteButton);


        SystemButtonRuleContext bulkDeleteButton = new SystemButtonRuleContext();
        bulkDeleteButton.setName("Delete");
        bulkDeleteButton.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        bulkDeleteButton.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        bulkDeleteButton.setIdentifier("delete_bulk");
        bulkDeleteButton.setPermissionRequired(true);
        bulkDeleteButton.setPermission("DELETE");
        bulkDeleteButton.setCriteria(getDeleteCriteria());
        SystemButtonApi.addSystemButton(FacilioConstants.TimeSheet.TIME_SHEET,bulkDeleteButton);

        SystemButtonApi.addExportAsCSV(FacilioConstants.TimeSheet.TIME_SHEET);
        SystemButtonApi.addExportAsExcel(FacilioConstants.TimeSheet.TIME_SHEET);



    }

    public static Criteria getEditCriteria() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        LookupField status = new LookupField();
        status.setName("status");
        status.setColumnName("STATUS");
        status.setDataType(FieldType.LOOKUP);
        status.setModule(moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET));
        status.setLookupModule(moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET_STATUS));

        LookupField recordLocked = new LookupField();
        recordLocked.setName("recordLocked");
        recordLocked.setColumnName("RECORD_LOCKED");
        recordLocked.setDataType(FieldType.BOOLEAN);
        recordLocked.setModule(moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET_STATUS));

        Criteria oneLevelCriteria=new Criteria();
        oneLevelCriteria.addAndCondition(CriteriaAPI.getCondition(recordLocked,String.valueOf(false), BooleanOperators.IS));

        Condition statusCondition=new Condition();
        statusCondition.setOperator(LookupOperator.LOOKUP);
        statusCondition.setField(status);
        statusCondition.setCriteriaValue(oneLevelCriteria);

        Criteria statusCriteria = new Criteria();
        statusCriteria.addAndCondition(statusCondition);

        return statusCriteria;
    }

    public static Criteria getDeleteCriteria() throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        LookupField status = new LookupField();
        status.setName("status");
        status.setColumnName("STATUS");
        status.setDataType(FieldType.LOOKUP);
        status.setModule(moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET));
        status.setLookupModule(moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET_STATUS));

        LookupField recordLocked = new LookupField();
        recordLocked.setName("deleteLocked");
        recordLocked.setColumnName("DELETE_LOCKED");
        recordLocked.setDataType(FieldType.BOOLEAN);
        recordLocked.setModule(moduleBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET_STATUS));

        Criteria oneLevelCriteria=new Criteria();
        oneLevelCriteria.addAndCondition(CriteriaAPI.getCondition(recordLocked,String.valueOf(false),BooleanOperators.IS));

        Condition statusCondition=new Condition();
        statusCondition.setOperator(LookupOperator.LOOKUP);
        statusCondition.setField(status);
        statusCondition.setCriteriaValue(oneLevelCriteria);

        Criteria statusCriteria = new Criteria();
        statusCriteria.addAndCondition(statusCondition);

        return statusCriteria;


    }

}
