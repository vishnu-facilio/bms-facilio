package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.bmsconsoleV3.context.PeopleTypeField;
import com.facilio.bmsconsoleV3.signup.jobPlan.AddJobPlanModule;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.qa.context.ResponseContext;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.utility.UtilityDisputeType;
import com.facilio.utility.context.UtilityDisputeContext;
import com.facilio.v3.context.Constants;
import org.json.simple.JSONObject;

import java.util.*;

public class UtilityDisputeModule extends BaseModuleConfig {
    public static List<String> supportedApps = Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.IWMS_APP);

    public UtilityDisputeModule(){
        setModuleName(FacilioConstants.UTILITY_DISPUTE);
    }

    @Override
    public void addData() throws Exception {
        ModuleBean bean = Constants.getModBean();

        List<FacilioModule> modules = new ArrayList<>();
        FacilioModule utilityDispute = addUtilityDispute();
        modules.add(utilityDispute);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
        addModuleChain.execute();

        addDefaultStateFlow(utilityDispute);
        addSystemButtons();

    }

    public FacilioModule addUtilityDispute() throws Exception{

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean", AccountUtil.getCurrentOrg().getOrgId());

        FacilioModule module = new FacilioModule("utilityDispute", "Utility Dispute", "Utility_Dispute",FacilioModule.ModuleType.BASE_ENTITY,true);

        List<FacilioField> fields = new ArrayList<>();

        LookupField utilityIntegrationCustomer = FieldFactory.getDefaultField("account", "Account", "UTILITY_INTEGRATION_CUSTOMER", FieldType.LOOKUP);
        utilityIntegrationCustomer.setLookupModule(bean.getModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER));
        fields.add(utilityIntegrationCustomer);

        LookupField utilityBill = FieldFactory.getDefaultField("utilityBill", "Utility Bill ", "UTILITY_INTEGRATION_BILL", FieldType.LOOKUP);
        utilityBill.setLookupModule(bean.getModule(FacilioConstants.UTILITY_INTEGRATION_BILLS));
        fields.add(utilityBill);

        SystemEnumField type = (SystemEnumField) FieldFactory.getDefaultField("type", "UtilityDisputeType", "TYPE", FieldType.SYSTEM_ENUM);
        type.setEnumName("UtilityDisputeType");
        fields.add(type);

        StringField utilityType = (StringField) FieldFactory.getDefaultField("utilityType", "Utility Type", "UTILITY_TYPE", FieldType.STRING);
        fields.add(utilityType);

        SystemEnumField billStatus = (SystemEnumField) FieldFactory.getDefaultField("billStatus", "Bill Status", "BILL_STATUS", FieldType.SYSTEM_ENUM);
        billStatus.setEnumName("BillStatus");
        fields.add(billStatus);

        DateField billDate =  FieldFactory.getDefaultField("billDate", "Bill Missing Date", "BILL_DATE", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(billDate);

        DateField cutOffDate =  FieldFactory.getDefaultField("expires", "Cut Off Date", "CUT_OFF_DATE", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(cutOffDate);

        StringField supplier = (StringField) FieldFactory.getDefaultField("supplier", "Supplier", "SUPPLIER", FieldType.STRING);
        fields.add(supplier);

        StringField accountNumber = (StringField) FieldFactory.getDefaultField("accountNumber", "Account Number", "ACCOUNT_NUMBER", FieldType.STRING);
        fields.add(accountNumber);

        NumberField billTotal = FieldFactory.getDefaultField("billTotal","Bill Total Cost","BILL_TOTAL_COST", FieldType.DECIMAL);
        fields.add(billTotal);

        StringField subject = (StringField) FieldFactory.getDefaultField("subject", "Subject", "SUBJECT", FieldType.STRING,true);
        fields.add(subject);

        DateField terminatedOn =  FieldFactory.getDefaultField("terminatedOn", "Terminated On", "TERMINATED_ON", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(terminatedOn);

        StringField tariffToBeApplied = (StringField) FieldFactory.getDefaultField("tariffToBeApplied", "Tariff to be applied", "TARIFF_TO_BE_APPLIED", FieldType.STRING);
        fields.add(tariffToBeApplied);

        StringField tariffApplied = (StringField) FieldFactory.getDefaultField("tariffApplied", "Tariff applied", "TARIFF_APPLIED", FieldType.STRING);
        fields.add(tariffApplied);

        DateField resolvedTime =  FieldFactory.getDefaultField("resolvedTime", "Resolved Time ", "RESOLVED_TIME", FieldType.DATE_TIME,FacilioField.FieldDisplayType.DATETIME);
        fields.add(resolvedTime);

        LookupField resolvedBy = FieldFactory.getDefaultField("resolvedBy","Resolved By","RESOLVED_BY", FieldType.LOOKUP);
        resolvedBy.setLookupModule(bean.getModule(FacilioConstants.ContextNames.PEOPLE));

        fields.add(resolvedBy);

        NumberField actualMeterConsumption = FieldFactory.getDefaultField("actualMeterConsumption","Actual Meter Consumption","ACTUAL_METER_CONSUMPTION", FieldType.DECIMAL);
        fields.add(actualMeterConsumption);

        NumberField billMeterConsumption = FieldFactory.getDefaultField("billMeterConsumption","Bill Meter  Consumption","BILL_METER_CONSUMPTION", FieldType.DECIMAL);
        fields.add(billMeterConsumption);

        NumberField disputedConsumption = FieldFactory.getDefaultField("disputedConsumption","Disputed  Consumption","DISPUTED_CONSUMPTION", FieldType.DECIMAL);
        fields.add(disputedConsumption);

        NumberField expectedCost = FieldFactory.getDefaultField("expectedCost","Expected Cost","EXPECTED_COST", FieldType.DECIMAL);
        fields.add(expectedCost);
        NumberField actualCost = FieldFactory.getDefaultField("actualCost","Actual Cost ","ACTUAL_COST", FieldType.DECIMAL);
        fields.add(actualCost);
        NumberField differenceInCost = FieldFactory.getDefaultField("differenceInCost","Difference in cost","DIFF_IN_COST", FieldType.DECIMAL);
        fields.add(differenceInCost);

        LookupField moduleStateField = (LookupField) FieldFactory.getField("moduleState", "Status", "MODULE_STATE", module, FieldType.LOOKUP);
        moduleStateField.setDefault(true);
        moduleStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        moduleStateField.setLookupModule(bean.getModule("ticketstatus"));
        fields.add(moduleStateField);

        FacilioField stateFlowIdField = FieldFactory.getField("stateFlowId", "State Flow Id", "STATE_FLOW_ID", module, FieldType.NUMBER);
        stateFlowIdField.setDefault(true);
        stateFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        fields.add(stateFlowIdField);

        LookupField approvalStateField = (LookupField) FieldFactory.getField("approvalStatus", "Approval Status", "APPROVAL_STATE", module, FieldType.LOOKUP);
        approvalStateField.setDefault(true);
        approvalStateField.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        approvalStateField.setLookupModule(bean.getModule("ticketstatus"));
        fields.add(approvalStateField);

        FacilioField approvalFlowIdField = FieldFactory.getField("approvalFlowId", "Approval Flow Id", "APPROVAL_FLOW_ID", module, FieldType.NUMBER);
        approvalFlowIdField.setDefault(true);
        approvalFlowIdField.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        fields.add(approvalFlowIdField);


        module.setFields(fields);
        return module;
    }
//    public List<Map<String, Object>> getViewsAndGroups() {
//        List<Map<String, Object>> groupVsViews = new ArrayList<>();
//        Map<String, Object> groupDetails;
//
//        int order = 1;
//        ArrayList<FacilioView> dispute = new ArrayList<FacilioView>();
//        dispute.add(getAllDisputeView().setOrder(order++));
//
//        groupDetails = new HashMap<>();
//        groupDetails.put("name", "systemviews");
//        groupDetails.put("displayName", "System Views");
//        groupDetails.put("moduleName", FacilioConstants.UTILITY_DISPUTE);
//        groupDetails.put("views", dispute);
//        groupDetails.put("appLinkNames", UtilityDisputeModule.supportedApps);
//        groupVsViews.add(groupDetails);
//
//        return groupVsViews;
//    }
//
//    private static FacilioView getAllDisputeView() {
//
//        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "ID", FieldType.NUMBER), false));
//
//        FacilioView allView = new FacilioView();
//        allView.setName("all");
//        allView.setDisplayName("All Utility Dispute");
//        allView.setModuleName(FacilioConstants.UTILITY_DISPUTE);
//        allView.setSortFields(sortFields);
//        allView.setAppLinkNames(UtilityDisputeModule.supportedApps);
//
//        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
//        appDomains.add(AppDomain.AppDomainType.FACILIO);
//
//        return allView;
//    }

    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> utilityDisputerModule = new ArrayList<FacilioView>();
        utilityDisputerModule.add(getUtilityDisputeViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.UTILITY_DISPUTE);
        groupDetails.put("views", utilityDisputerModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getUtilityDisputeViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "utilityDispute.ID", FieldType.NUMBER), true));

        FacilioView utilityDisputeView = new FacilioView();
        utilityDisputeView.setName("all");
        utilityDisputeView.setDisplayName("Utility Dispute");

        utilityDisputeView.setModuleName(FacilioConstants.UTILITY_DISPUTE);
        utilityDisputeView.setSortFields(sortFields);
        utilityDisputeView.setAppLinkNames(UtilityDisputeModule.supportedApps);

        List<ViewField> utilityDisputeViewFields = new ArrayList<>();

        utilityDisputeViewFields.add(new ViewField("subject","Subject"));
        utilityDisputeViewFields.add(new ViewField("account","Account"));
        utilityDisputeViewFields.add(new ViewField("utilityBill","Utility Bill"));
        utilityDisputeViewFields.add(new ViewField("billStatus","BillStatus"));
        utilityDisputeViewFields.add(new ViewField("type","UtilityDisputeType"));




        utilityDisputeView.setFields(utilityDisputeViewFields);

        return utilityDisputeView;
    }
    public void addDefaultStateFlow(FacilioModule disputeModule) throws Exception {

        FacilioStatus createdStatus = getFacilioStatus(disputeModule, "underDispute", "Under Dispute", FacilioStatus.StatusType.OPEN, Boolean.FALSE);
        FacilioStatus resolvedStatus = getFacilioStatus(disputeModule, "resolved", "Resolved", FacilioStatus.StatusType.CLOSED, Boolean.FALSE);

        StateFlowRuleContext stateFlowRuleContext = new StateFlowRuleContext();
        stateFlowRuleContext.setName("Default Stateflow");
        stateFlowRuleContext.setModuleId(disputeModule.getModuleId());
        stateFlowRuleContext.setModule(disputeModule);
        stateFlowRuleContext.setActivityType(EventType.CREATE);
        stateFlowRuleContext.setExecutionOrder(1);
        stateFlowRuleContext.setStatus(true);
        stateFlowRuleContext.setDefaltStateFlow(true);
        stateFlowRuleContext.setDefaultStateId(createdStatus.getId());
        stateFlowRuleContext.setRuleType(WorkflowRuleContext.RuleType.STATE_FLOW);
        WorkflowRuleAPI.addWorkflowRule(stateFlowRuleContext);

//        Criteria completionCtriteria = new Criteria();
//        completionCtriteria.addAndCondition(CriteriaAPI.getCondition("BILL_STATUS", "billStatus", UtilityDisputeContext.BillStatus.RESOLVED.getIndex()+"", EnumOperators.IS));
//
//        addStateflowTransitionContext(disputeModule, stateFlowRuleContext, "Resolved", createdStatus, resolvedStatus, AbstractStateTransitionRuleContext.TransitionType.CONDITIONED,completionCtriteria,null);

    }
    private void addStateflowTransitionContext(FacilioModule module,StateFlowRuleContext parentStateFlow,String name,FacilioStatus fromStatus,FacilioStatus toStatus,AbstractStateTransitionRuleContext.TransitionType transitionType,Criteria criteria,List<ActionContext> actions) throws Exception {

        StateflowTransitionContext stateFlowTransitionContext = new StateflowTransitionContext();
        stateFlowTransitionContext.setName(name);
        stateFlowTransitionContext.setModule(module);
        stateFlowTransitionContext.setModuleId(module.getModuleId());
        stateFlowTransitionContext.setActivityType(EventType.STATE_TRANSITION);
        stateFlowTransitionContext.setExecutionOrder(1);
        stateFlowTransitionContext.setButtonType(1);
        stateFlowTransitionContext.setFromStateId(fromStatus.getId());
        stateFlowTransitionContext.setToStateId(toStatus.getId());
        stateFlowTransitionContext.setRuleType(WorkflowRuleContext.RuleType.STATE_RULE);
        stateFlowTransitionContext.setType(transitionType);
        stateFlowTransitionContext.setStateFlowId(parentStateFlow.getId());
        stateFlowTransitionContext.setCriteria(criteria);
        WorkflowRuleAPI.addWorkflowRule(stateFlowTransitionContext);


//        if (actions != null && !actions.isEmpty()) {
//            actions = ActionAPI.addActions(actions, stateFlowTransitionContext);
//            if(stateFlowTransitionContext != null) {
//                ActionAPI.addWorkflowRuleActionRel(stateFlowTransitionContext.getId(), actions);
//                stateFlowTransitionContext.setActions(actions);
//            }
//        }
        FacilioChain chain =TransactionChainFactory.getAddOrUpdateStateFlowTransition();
        chain.getContext().put(FacilioConstants.ContextNames.WORKFLOW_RULE,stateFlowTransitionContext);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE,module);
        chain.execute();

        //return stateFlowTransitionContext;
    }
    private FacilioStatus getFacilioStatus(FacilioModule module, String status, String displayName, FacilioStatus.StatusType status1, Boolean timerEnabled) throws Exception {

        FacilioStatus statusObj = new FacilioStatus();
        statusObj.setStatus(status);
        statusObj.setDisplayName(displayName);
        statusObj.setTypeCode(status1.getIntVal());
        statusObj.setTimerEnabled(timerEnabled);
        TicketAPI.addStatus(statusObj, module);

        return statusObj;
    }

    private static void addSystemButtons() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> stFields = modBean.getAllFields(FacilioConstants.UTILITY_DISPUTE);
        Map<String,FacilioField> stFieldMap = FieldFactory.getAsMap(stFields);

//        SystemButtonRuleContext underDispute = new SystemButtonRuleContext();
//        underDispute.setName("Under Dispute");
//        underDispute.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
//        underDispute.setIdentifier(FacilioConstants.UNDER_DISPUTE);
//        underDispute.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
//        underDispute.setPermission(AccountConstants.ModulePermission.UPDATE.name());
//        underDispute.setPermissionRequired(true);
//        Criteria disputeCriteria = new Criteria();
//        disputeCriteria.addAndCondition(CriteriaAPI.getCondition(stFieldMap.get("billStatus"),String.valueOf(UtilityDisputeContext.BillStatus.DISPUTE), EnumOperators.IS));
//        underDispute.setCriteria(disputeCriteria);
//        SystemButtonApi.addSystemButton(FacilioConstants.UTILITY_DISPUTE,underDispute);

        SystemButtonRuleContext resolveDispute = new SystemButtonRuleContext();
        resolveDispute.setName("Resolve Dispute");
        resolveDispute.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        resolveDispute.setIdentifier(FacilioConstants.RESOLVE_DISPUTE);
        resolveDispute.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        resolveDispute.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        resolveDispute.setPermissionRequired(true);
        Criteria resolveCriteria = new Criteria();
        resolveCriteria.addAndCondition(CriteriaAPI.getCondition(stFieldMap.get("billStatus"),String.valueOf(UtilityDisputeContext.BillStatus.DISPUTE.getIndex()), EnumOperators.IS));
        resolveDispute.setCriteria(resolveCriteria);
        SystemButtonApi.addSystemButton(FacilioConstants.UTILITY_DISPUTE,resolveDispute);

    }
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String, List<PagesContext>> pageTemp = new HashMap<>();
        pageTemp.put(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, getSystemPage());
        return  pageTemp;
    }
    private static List<PagesContext> getSystemPage() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule disputeModule = modBean.getModule(FacilioConstants.UTILITY_DISPUTE);

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.UTILITY_DISPUTE_ACTIVITY);
        List<PagesContext> disputePages = new ArrayList<>();
        //bill missing
        PagesContext billMissingTemplatePage = new PagesContext(null, null, "", getBillMissingCriteria(), true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("utilitydisputesummary", "SUMMARY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("utilitydisputesummaryfields", null, null)
                .addWidget("utilitydisputesummarywidget", "Dispute  Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getBillMissingSummaryWidgetDetails(FacilioConstants.UTILITY_DISPUTE))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("utilitydisputehistory", "HISTORY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("history", "History ", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();


        PagesContext accountTerminatedTemplatePage = new PagesContext(null, null, "", getAccountRevokedCriteria(), true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("utilitydisputesummary", "SUMMARY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("utilitydisputesummaryfields", null, null)
                .addWidget("utilitydisputesummarywidget", "Dispute  Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getAccountTerminatedSummaryWidgetDetails(FacilioConstants.UTILITY_DISPUTE))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("utilitydisputehistory", "HISTORY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("history", "History ", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

        PagesContext consumptionTemplatePage = new PagesContext(null, null, "", getConsumptionMismatchCriteria(), true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("utilitydisputesummary", "SUMMARY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("utilitydisputesummaryfields", null, null)
                .addWidget("utilitydisputesummarywidget", "Dispute  Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getConsumptionMismatchSummaryWidgetDetails(FacilioConstants.UTILITY_DISPUTE))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("utilitydisputehistory", "HISTORY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("history", "History ", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

        PagesContext tariffTemplatePage = new PagesContext(null, null, "", getTariffMismatchCriteria(), true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("utilitydisputesummary", "SUMMARY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("utilitydisputesummaryfields", null, null)
                .addWidget("utilitydisputesummarywidget", "Dispute  Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getTariffMismatchSummaryWidgetDetails(FacilioConstants.UTILITY_DISPUTE))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("utilitydisputehistory", "HISTORY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("history", "History ", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

        PagesContext costMismatchTemplatePage = new PagesContext(null, null, "", getCostMismatch(), true, false, false)
                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("utilitydisputesummary", "SUMMARY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("utilitydisputesummaryfields", null, null)
                .addWidget("utilitydisputesummarywidget", "Dispute  Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getCostMismatchSummaryWidgetDetails(FacilioConstants.UTILITY_DISPUTE))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getSummaryWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("utilitydisputehistory", "HISTORY", PageTabContext.TabType.SIMPLE,true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("history", "History ", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone();

        disputePages.add(billMissingTemplatePage);
        disputePages.add(accountTerminatedTemplatePage);
        disputePages.add(consumptionTemplatePage);
        disputePages.add(tariffTemplatePage);
        disputePages.add(costMismatchTemplatePage);

        return disputePages;
    }
    private static Criteria getBillMissingCriteria() {
        Criteria criteria = new Criteria();
        Condition billMissing = new Condition();
        billMissing.setFieldName("type");
        billMissing.setColumnName("Utility_Dispute.TYPE");
        billMissing.setOperator(EnumOperators.IS);
        billMissing.setValue(UtilityDisputeType.BILL_MISSING.getIndex()+"");
        billMissing.setModuleName(FacilioConstants.UTILITY_DISPUTE);
        criteria.addAndCondition(billMissing);
        return criteria;
    }
    private static Criteria getAccountRevokedCriteria() {
        Criteria criteria = new Criteria();
        Condition terminated = new Condition();
        terminated.setFieldName("type");
        terminated.setColumnName("Utility_Dispute.TYPE");
        terminated.setOperator(EnumOperators.IS);
        terminated.setValue(UtilityDisputeType.BILL_FOR_TERMINATED_ACCOUNT.getIndex()+"");
        terminated.setModuleName(FacilioConstants.UTILITY_DISPUTE);
        criteria.addAndCondition(terminated);
        return criteria;
    }
    private static Criteria getConsumptionMismatchCriteria() {
        Criteria criteria = new Criteria();
        Condition consumption = new Condition();
        consumption.setFieldName("type");
        consumption.setColumnName("Utility_Dispute.TYPE");
        consumption.setOperator(EnumOperators.IS);
        consumption.setValue(UtilityDisputeType.CONSUMPTION_READING_MISMATCH.getIndex()+"");
        consumption.setModuleName(FacilioConstants.UTILITY_DISPUTE);
        criteria.addAndCondition(consumption);
        return criteria;
    }
    private static Criteria getTariffMismatchCriteria() {
        Criteria criteria = new Criteria();
        Condition tariff = new Condition();
        tariff.setFieldName("type");
        tariff.setColumnName("Utility_Dispute.TYPE");
        tariff.setOperator(EnumOperators.IS);
        tariff.setValue(UtilityDisputeType.TARIFF_MAPPING_MISMATCH.getIndex()+"");
        tariff.setModuleName(FacilioConstants.UTILITY_DISPUTE);
        criteria.addAndCondition(tariff);
        return criteria;
    }
    private static Criteria getCostMismatch() {
        Criteria criteria = new Criteria();
        Condition cost = new Condition();
        cost.setFieldName("type");
        cost.setColumnName("Utility_Dispute.TYPE");
        cost.setOperator(EnumOperators.IS);
        cost.setValue(UtilityDisputeType.COST_MISMATCH.getIndex()+"");
        cost.setModuleName(FacilioConstants.UTILITY_DISPUTE);
        criteria.addAndCondition(cost);
        return criteria;
    }

    private static JSONObject getBillMissingSummaryWidgetDetails(String moduleName) throws Exception{
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField dateField = moduleBean.getField("billDate", moduleName);
        FacilioField accNoField = moduleBean.getField("account", moduleName);
        FacilioField supplierField = moduleBean.getField("utilityBill", moduleName);
        FacilioField typeField = moduleBean.getField("type", moduleName);


        FacilioField createdField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField createdByField = moduleBean.getField("sysCreatedBy",moduleName);
        FacilioField modifiedField = moduleBean.getField("sysModifiedBy",moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime",moduleName);


        SummaryWidget pageWidget = new SummaryWidget();

        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, accNoField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, supplierField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, typeField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, dateField, 1, 4, 1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("Primary Information");
        widgetGroup.setColumns(4);


        SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup1, createdField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, createdByField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, modifiedField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, sysModifiedTimeField, 1, 4, 1);



        widgetGroup1.setName("moduleSystemDetails");
        widgetGroup1.setDisplayName("System Information");
        widgetGroup1.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup1);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP).getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }
    private static JSONObject getAccountTerminatedSummaryWidgetDetails(String moduleName) throws Exception{
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField dateField = moduleBean.getField("terminatedOn", moduleName);
        FacilioField accNoField = moduleBean.getField("account", moduleName);
        FacilioField supplierField = moduleBean.getField("utilityBill", moduleName);
        FacilioField typeField = moduleBean.getField("type", moduleName);


        FacilioField createdField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField createdByField = moduleBean.getField("sysCreatedBy",moduleName);
        FacilioField modifiedField = moduleBean.getField("sysModifiedBy",moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime",moduleName);


        SummaryWidget pageWidget = new SummaryWidget();

        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup, accNoField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, supplierField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, typeField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, dateField, 1, 4, 1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("Primary Information");
        widgetGroup.setColumns(4);


        SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup1, createdField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, createdByField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, modifiedField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, sysModifiedTimeField, 1, 4, 1);
        


        widgetGroup1.setName("moduleSystemDetails");
        widgetGroup1.setDisplayName("System Information");
        widgetGroup1.setColumns(4);

        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup1);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP).getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);
    }
    private static JSONObject getConsumptionMismatchSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField accNoField = moduleBean.getField("account", moduleName);
        FacilioField supplierField = moduleBean.getField("utilityBill", moduleName);
        FacilioField typeField = moduleBean.getField("type", moduleName);


        FacilioField createdField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField createdByField = moduleBean.getField("sysCreatedBy",moduleName);
        FacilioField modifiedField = moduleBean.getField("sysModifiedBy",moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime",moduleName);
        FacilioField resolvedBy = moduleBean.getField("resolvedBy",moduleName);

        FacilioField actualMeterConsumption = moduleBean.getField("actualMeterConsumption",moduleName);
        FacilioField billMeterConsumption = moduleBean.getField("billMeterConsumption",moduleName);
        FacilioField disputedConsumption = moduleBean.getField("disputedConsumption",moduleName);


        SummaryWidget pageWidget = new SummaryWidget();

        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();


        addSummaryFieldInWidgetGroup(widgetGroup, accNoField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, supplierField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, typeField, 1, 3, 1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("Primary Information");
        widgetGroup.setColumns(4);


        SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup1, createdField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, createdByField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, modifiedField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, sysModifiedTimeField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, resolvedBy, 2, 1, 1);


        widgetGroup1.setName("moduleSystemDetails");
        widgetGroup1.setDisplayName("System Information");
        widgetGroup1.setColumns(4);

        SummaryWidgetGroup widgetGroup2 = new SummaryWidgetGroup();

        addSummaryFieldInWidgetGroup(widgetGroup2, actualMeterConsumption, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, billMeterConsumption, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, disputedConsumption, 1, 3, 1);


        widgetGroup2.setName("otherDetails");
        widgetGroup2.setDisplayName("Dispute Information");
        widgetGroup2.setColumns(4);


        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup2);
        widgetGroupList.add(widgetGroup1);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP).getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }
    private static JSONObject getTariffMismatchSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField accNoField = moduleBean.getField("account", moduleName);
        FacilioField supplierField = moduleBean.getField("utilityBill", moduleName);
        FacilioField typeField = moduleBean.getField("type", moduleName);


        FacilioField createdField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField createdByField = moduleBean.getField("sysCreatedBy",moduleName);
        FacilioField modifiedField = moduleBean.getField("sysModifiedBy",moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime",moduleName);
        FacilioField resolvedBy = moduleBean.getField("resolvedBy",moduleName);

        FacilioField tariffToBeApplied = moduleBean.getField("tariffToBeApplied",moduleName);
        FacilioField tariffApplied = moduleBean.getField("tariffApplied",moduleName);


        SummaryWidget pageWidget = new SummaryWidget();

        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();


        addSummaryFieldInWidgetGroup(widgetGroup, accNoField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, supplierField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, typeField, 1, 3, 1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("Primary Information");
        widgetGroup.setColumns(4);


        SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup1, createdField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, createdByField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, modifiedField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, sysModifiedTimeField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, resolvedBy, 2, 1, 1);


        widgetGroup1.setName("moduleSystemDetails");
        widgetGroup1.setDisplayName("System Information");
        widgetGroup1.setColumns(4);

        SummaryWidgetGroup widgetGroup2 = new SummaryWidgetGroup();


        addSummaryFieldInWidgetGroup(widgetGroup2, tariffToBeApplied, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, tariffApplied, 1, 2, 1);



        widgetGroup2.setName("otherDetails");
        widgetGroup2.setDisplayName("Dispute Information");
        widgetGroup2.setColumns(4);


        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup2);
        widgetGroupList.add(widgetGroup1);

        pageWidget.setDisplayName("");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP).getId());
        pageWidget.setGroups(widgetGroupList);

        return FieldUtil.getAsJSON(pageWidget);

    }
    private static JSONObject getCostMismatchSummaryWidgetDetails(String moduleName) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        FacilioField accNoField = moduleBean.getField("account", moduleName);
        FacilioField supplierField = moduleBean.getField("utilityBill", moduleName);
        FacilioField typeField = moduleBean.getField("type", moduleName);


        FacilioField createdField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField createdByField = moduleBean.getField("sysCreatedBy",moduleName);
        FacilioField modifiedField = moduleBean.getField("sysModifiedBy",moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime",moduleName);
        FacilioField resolvedBy = moduleBean.getField("resolvedBy",moduleName);


        FacilioField expectedCost = moduleBean.getField("expectedCost",moduleName);
        FacilioField actualCost = moduleBean.getField("actualCost",moduleName);
        FacilioField differenceInCost = moduleBean.getField("differenceInCost",moduleName);


        SummaryWidget pageWidget = new SummaryWidget();

        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();


        addSummaryFieldInWidgetGroup(widgetGroup, accNoField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, supplierField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, typeField, 1, 3, 1);


        widgetGroup.setName("moduleDetails");
        widgetGroup.setDisplayName("Primary Information");
        widgetGroup.setColumns(4);



        SummaryWidgetGroup widgetGroup1 = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(widgetGroup1, createdField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, createdByField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, modifiedField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, sysModifiedTimeField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup1, resolvedBy, 2, 1, 1);


        widgetGroup1.setName("moduleSystemDetails");
        widgetGroup1.setDisplayName("System Information");
        widgetGroup1.setColumns(4);

        SummaryWidgetGroup widgetGroup2 = new SummaryWidgetGroup();


        addSummaryFieldInWidgetGroup(widgetGroup2, expectedCost, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, actualCost, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup2, differenceInCost, 1, 3, 1);


        widgetGroup2.setName("otherDetails");
        widgetGroup2.setDisplayName("Dispute Information");
        widgetGroup2.setColumns(4);


        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(widgetGroup2);
        widgetGroupList.add(widgetGroup1);

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
        commentWidgetParam.put("notesModuleName", FacilioConstants.UTILITY_DISPUTE_NOTES);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", FacilioConstants.UTILITY_DISPUTE_ATTACHMENTS);

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

