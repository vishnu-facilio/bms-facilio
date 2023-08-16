package com.facilio.fsm.signup;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fsm.context.ServiceAppointmentTicketStatusContext;
import com.facilio.fsm.util.ServiceAppointmentUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.qa.context.ResponseContext;
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

        List<FacilioField> timeSheetFields = new ArrayList<>();

        NumberField localId = new NumberField(timeSheetModule,"localId","Id", FacilioField.FieldDisplayType.NUMBER,"LOCAL_ID",FieldType.NUMBER,false,false,true,false);
        timeSheetFields.add(localId);
        ModuleLocalIdUtil.insertModuleLocalId(FacilioConstants.TimeSheet.TIME_SHEET);

        DateField startTime = new DateField(timeSheetModule,"startTime","Start Time", FacilioField.FieldDisplayType.DATETIME,"START_TIME", FieldType.DATE_TIME,true,false,true,false);
        timeSheetFields.add(startTime);

        DateField endTime = new DateField(timeSheetModule,"endTime","End Time", FacilioField.FieldDisplayType.DATETIME,"END_TIME",FieldType.DATE_TIME,true,false,true,false);
        timeSheetFields.add(endTime);

        FacilioField actualDuration = FieldFactory.getDefaultField("duration","Duration","DURATION", FieldType.NUMBER);
        actualDuration.setDisplayType(FacilioField.FieldDisplayType.DURATION);
        timeSheetFields.add(actualDuration);

        LookupField fieldAgent = new LookupField(timeSheetModule,"fieldAgent","Field Agent", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"PEOPLE_ID",FieldType.LOOKUP,true,false,true,true,"Field Agent", Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE));
        timeSheetFields.add(fieldAgent);

        LookupField serviceAppointment = new LookupField(timeSheetModule,"serviceAppointment","Service Appointment", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_APPOINTMENT_ID",FieldType.LOOKUP,true,false,true,false,"Service Appointment", Constants.getModBean().getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
        timeSheetFields.add(serviceAppointment);

        LookupField serviceOrder = new LookupField(timeSheetModule,"serviceOrder","Service Order", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"SERVICE_ORDER_ID",FieldType.LOOKUP,true,false,true,false,"Service Order", Constants.getModBean().getModule(FacilioConstants.ContextNames.SERVICE_ORDER));
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
        addStateFlow();
        addSystemButtons();

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
                .addPage("timeSheet", "Time Sheet", "", null, isTemplate, isDefault, false)
                .addWebTab("timesheetsummary", "Summary", true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("timesheetsummaryfields", null, null)
                .addWidget("timesheetysummaryfieldswidget", "Time Sheet Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(FacilioConstants.TimeSheet.TIME_SHEET))
                .widgetDone()
                .sectionDone()
                .addSection("timesheettasklist",null,null)
                .addWidget("timesheettasklistwidget","Tasks",PageWidget.WidgetType.TIMESHEET_TASKS,"timesheettasks_23_12",0,0,null,null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addWebTab("timesheethistory", "History", true, null)
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
        commentWidgetParam.put("notesModuleName", FacilioConstants.TimeSheet.TIME_SHEET_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.TimeSheet.TIME_SHEET_ATTACHMENTS);

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

    private void addStateFlow() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule timeSheetModule = modBean.getModule(FacilioConstants.TimeSheet.TIME_SHEET);


        FacilioStatus inProgressStatus = new FacilioStatus();
        inProgressStatus.setStatus("inProgress");
        inProgressStatus.setDisplayName("In Progress");
        inProgressStatus.setTypeCode(1);
        TicketAPI.addStatus(inProgressStatus, timeSheetModule);

        FacilioStatus completedStatus = new FacilioStatus();
        completedStatus.setStatus("completed");
        completedStatus.setDisplayName("Completed");
        completedStatus.setTypeCode(2);
        completedStatus.setRecordLocked(true);
        TicketAPI.addStatus(completedStatus, timeSheetModule);

        StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
        stateFlowRuleContext.setName("Default Stateflow");
        stateFlowRuleContext.setModuleId(timeSheetModule.getModuleId());
        stateFlowRuleContext.setModule(timeSheetModule);
        stateFlowRuleContext.setActivityType(EventType.CREATE);
        stateFlowRuleContext.setExecutionOrder(1);
        stateFlowRuleContext.setStatus(true);
        stateFlowRuleContext.setDefaltStateFlow(true);
        stateFlowRuleContext.setDefaultStateId(inProgressStatus.getId());
        stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
        WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

        Criteria completionCriteria = new Criteria();
        completionCriteria.addAndCondition(CriteriaAPI.getCondition("END_TIME", "endTime", null, CommonOperators.IS_NOT_EMPTY));

        addStateflowTransitionContext(timeSheetModule, stateFlowRuleContext, "Complete Inspection", inProgressStatus,completedStatus, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,completionCriteria,null);
    }

    private StateflowTransitionContext addStateflowTransitionContext(FacilioModule module,StateFlowRuleContext parentStateFlow,String name,FacilioStatus fromStatus,FacilioStatus toStatus,AbstractStateTransitionRuleContext.TransitionType transitionType,Criteria criteria,List<ActionContext> actions) throws Exception {

        StateflowTransitionContext stateFlowTransitionContext = new StateflowTransitionContext();
        stateFlowTransitionContext.setName(name);
        stateFlowTransitionContext.setModule(module);
        stateFlowTransitionContext.setModuleId(module.getModuleId());
        stateFlowTransitionContext.setActivityType(EventType.CREATE);
        stateFlowTransitionContext.setExecutionOrder(1);
        stateFlowTransitionContext.setButtonType(1);
        stateFlowTransitionContext.setFromStateId(fromStatus.getId());
        stateFlowTransitionContext.setToStateId(toStatus.getId());
        stateFlowTransitionContext.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        stateFlowTransitionContext.setType(transitionType);
        stateFlowTransitionContext.setStateFlowId(parentStateFlow.getId());
        stateFlowTransitionContext.setCriteria(criteria);

        WorkflowRuleAPI.addWorkflowRule(stateFlowTransitionContext);

        if (actions != null && !actions.isEmpty()) {
            actions = ActionAPI.addActions(actions, stateFlowTransitionContext);
            if(stateFlowTransitionContext != null) {
                ActionAPI.addWorkflowRuleActionRel(stateFlowTransitionContext.getId(), actions);
                stateFlowTransitionContext.setActions(actions);
            }
        }

        return stateFlowTransitionContext;
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
        generalInformationFields.add(new FormField("serviceAppointment", FacilioField.FieldDisplayType.LOOKUP_SIMPLE,"Service Appointment",FormField.Required.REQUIRED,2,2));
        generalInformationFields.add(new FormField("startTime", FacilioField.FieldDisplayType.DATETIME, "Start Time", FormField.Required.REQUIRED, 3, 2));
        generalInformationFields.add(new FormField("endTime", FacilioField.FieldDisplayType.DATETIME, "End Time", FormField.Required.REQUIRED, 4, 2));

        FormSection generalSection = new FormSection("General Information", 1, generalInformationFields, true);
        generalSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormField> serviceTasksFields = new ArrayList<>();
        serviceTasksFields.add(new FormField("serviceTasks", FacilioField.FieldDisplayType.SERVICE_APPOINTMENT_TASKS, "Tasks", FormField.Required.REQUIRED, 1, 1));
        FormSection serviceTaskSection = new FormSection("Service Task Details", 2, serviceTasksFields, true);

        serviceTaskSection.setSectionType(FormSection.SectionType.FIELDS);

        List<FormSection> webTimeSheetFormSection = new ArrayList<>();
        webTimeSheetFormSection.add(generalSection);
        webTimeSheetFormSection.add(serviceTaskSection);

        timeSheetForm.setSections(webTimeSheetFormSection);
        timeSheetForm.setIsSystemForm(true);
        timeSheetForm.setType(FacilioForm.Type.FORM);
        List<FacilioForm> timeSheetModuleForms = new ArrayList<>();
        timeSheetModuleForms.add(timeSheetForm);
        return timeSheetModuleForms;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception{
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> timeSheetViews = new ArrayList<FacilioView>();
        timeSheetViews.add(getAllTimeSheetViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.TimeSheet.TIME_SHEET);
        groupDetails.put("views", timeSheetViews);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getAllTimeSheetViews() throws Exception {

        FacilioModule timeSheetModule = Constants.getModBean().getModule(FacilioConstants.TimeSheet.TIME_SHEET);
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getIdField(timeSheetModule), true));

        FacilioView allView = new FacilioView();
        allView.setName("alltimesheets");
        allView.setDisplayName("All Time Sheets");
        allView.setModuleName(FacilioConstants.TimeSheet.TIME_SHEET);
        allView.setSortFields(sortFields);
        allView.setAppLinkNames(TimeSheetModule.timeSheetSupportedApps);

        List<ViewField> timeSheetViewFields = new ArrayList<>();

        timeSheetViewFields.add(new ViewField("fieldAgent","Field Agent"));
        timeSheetViewFields.add(new ViewField("startTime","Start Time"));
        timeSheetViewFields.add(new ViewField("endTime","End Time"));
        timeSheetViewFields.add(new ViewField("serviceAppointment","Service Appointment"));
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

    }

}
