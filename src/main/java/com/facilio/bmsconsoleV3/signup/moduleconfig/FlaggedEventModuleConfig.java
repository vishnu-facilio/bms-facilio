package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.PeopleOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.criteria.operators.TeamOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import com.facilio.remotemonitoring.signup.*;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class FlaggedEventModuleConfig extends BaseModuleConfig {
    public FlaggedEventModuleConfig(){
        setModuleName(FlaggedEventModule.MODULE_NAME);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> flaggedAlarmViews = new ArrayList<FacilioView>();
        flaggedAlarmViews.add(getAllAlarms().setOrder(order++));
        flaggedAlarmViews.add(getOpenFlaggedAlarms().setOrder(order++));
        flaggedAlarmViews.add(getClosedFlaggedAlarms().setOrder(order++));
        flaggedAlarmViews.add(getWOCreatedFlaggedAlarms().setOrder(order++));
        flaggedAlarmViews.add(getAssignedToMyTeamFlaggedAlarms().setOrder(order++));
        flaggedAlarmViews.add(getAssignedToMeFlaggedAlarms().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FlaggedEventModule.MODULE_NAME);
        groupDetails.put("views", flaggedAlarmViews);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        groupDetails.put("appLinkNames", appLinkNames);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }


    private static FacilioView getAllAlarms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField sysCreatedTime = new FacilioField();
        sysCreatedTime.setName("sysCreatedTime");
        sysCreatedTime.setColumnName("SYS_CREATED_TIME");
        sysCreatedTime.setDataType(FieldType.NUMBER);
        sysCreatedTime.setModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Flagged Alarms");
        allView.setSortFields(Arrays.asList(new SortField(sysCreatedTime, false)));
        allView.setModuleName(FlaggedEventModule.MODULE_NAME);
        List<ViewField> viewFields = new ArrayList<>();

        viewFields.add(new ViewField("name", "Message"));
        viewFields.add(new ViewField("client", "Client"));
        viewFields.add(new ViewField("controller", "Controller"));
        viewFields.add(new ViewField(FilteredAlarmModule.FLAGGED_ALARM_PROCESS_FIELD_NAME, "Flagged Alarm Process"));
        viewFields.add(new ViewField("status", "Status"));
        viewFields.add(new ViewField("workorder", "Workorder"));

        allView.setFields(viewFields);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getOpenFlaggedAlarms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria criteria = new Criteria();
        FacilioField statusField = modBean.getField("status", FlaggedEventModule.MODULE_NAME);
        Condition openStatusCondition = new Condition();
        openStatusCondition.setField(statusField);
        openStatusCondition.setOperator(StringOperators.IS);
        openStatusCondition.setValue(FlaggedEventContext.FlaggedEventStatus.OPEN.getIndex());
        criteria.addAndCondition(openStatusCondition);

        FacilioField sysCreatedTime = new FacilioField();
        sysCreatedTime.setName("sysCreatedTime");
        sysCreatedTime.setColumnName("SYS_CREATED_TIME");
        sysCreatedTime.setDataType(FieldType.NUMBER);
        sysCreatedTime.setModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));

        FacilioView openFlaggedAlarmView = new FacilioView();
        openFlaggedAlarmView.setName("open");
        openFlaggedAlarmView.setDisplayName("Open Flagged Alarms");
        openFlaggedAlarmView.setCriteria(criteria);
        openFlaggedAlarmView.setSortFields(Arrays.asList(new SortField(sysCreatedTime, false)));
        openFlaggedAlarmView.setModuleName(FlaggedEventModule.MODULE_NAME);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("name", "Message"));
        viewFields.add(new ViewField("client", "Client"));
        viewFields.add(new ViewField("controller", "Controller"));
        viewFields.add(new ViewField(FilteredAlarmModule.FLAGGED_ALARM_PROCESS_FIELD_NAME, "Flagged Alarm Process"));
        viewFields.add(new ViewField("status", "Status"));
        viewFields.add(new ViewField("workorder", "Workorder"));

        openFlaggedAlarmView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        openFlaggedAlarmView.setAppLinkNames(appLinkNames);

        return openFlaggedAlarmView;
    }

    private static FacilioView getClosedFlaggedAlarms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria criteria = new Criteria();
        FacilioField statusField = modBean.getField("status", FlaggedEventModule.MODULE_NAME);

        Condition clearedStatusCondition = new Condition();
        clearedStatusCondition.setField(statusField);
        clearedStatusCondition.setOperator(StringOperators.IS);
        clearedStatusCondition.setValue(FlaggedEventContext.FlaggedEventStatus.CLEARED.getIndex());
        criteria.addOrCondition(clearedStatusCondition);

        Condition autoClosedStatusCondition = new Condition();
        autoClosedStatusCondition.setField(statusField);
        autoClosedStatusCondition.setOperator(StringOperators.IS);
        autoClosedStatusCondition.setValue(FlaggedEventContext.FlaggedEventStatus.AUTO_CLOSED.getIndex());
        criteria.addOrCondition(autoClosedStatusCondition);


        FacilioField sysCreatedTime = new FacilioField();
        sysCreatedTime.setName("sysCreatedTime");
        sysCreatedTime.setColumnName("SYS_CREATED_TIME");
        sysCreatedTime.setDataType(FieldType.NUMBER);
        sysCreatedTime.setModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));

        FacilioView closedFlaggedAlarmView = new FacilioView();
        closedFlaggedAlarmView.setName("closed");
        closedFlaggedAlarmView.setDisplayName("Closed Flagged Alarms");
        closedFlaggedAlarmView.setCriteria(criteria);
        closedFlaggedAlarmView.setSortFields(Arrays.asList(new SortField(sysCreatedTime, false)));
        closedFlaggedAlarmView.setModuleName(FlaggedEventModule.MODULE_NAME);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("name", "Message"));
        viewFields.add(new ViewField("client", "Client"));
        viewFields.add(new ViewField("controller", "Controller"));
        viewFields.add(new ViewField(FilteredAlarmModule.FLAGGED_ALARM_PROCESS_FIELD_NAME, "Flagged Alarm Process"));
        viewFields.add(new ViewField("status", "Status"));
        viewFields.add(new ViewField("workorder", "Workorder"));

        closedFlaggedAlarmView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        closedFlaggedAlarmView.setAppLinkNames(appLinkNames);

        return closedFlaggedAlarmView;
    }

    private static FacilioView getWOCreatedFlaggedAlarms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria criteria = new Criteria();

        FacilioField statusField = modBean.getField("status", FlaggedEventModule.MODULE_NAME);
        Condition woCreatedStatusCondition = new Condition();
        woCreatedStatusCondition.setField(statusField);
        woCreatedStatusCondition.setOperator(StringOperators.IS);
        woCreatedStatusCondition.setValue(FlaggedEventContext.FlaggedEventStatus.WORKORDER_CREATED.getIndex());
        criteria.addAndCondition(woCreatedStatusCondition);

        FacilioField sysCreatedTime = new FacilioField();
        sysCreatedTime.setName("sysCreatedTime");
        sysCreatedTime.setColumnName("SYS_CREATED_TIME");
        sysCreatedTime.setDataType(FieldType.NUMBER);
        sysCreatedTime.setModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));

        FacilioView woCreatedFlaggedAlarmView = new FacilioView();
        woCreatedFlaggedAlarmView.setName("wocreated");
        woCreatedFlaggedAlarmView.setDisplayName("Work Order Created Flagged Alarms");
        woCreatedFlaggedAlarmView.setCriteria(criteria);
        woCreatedFlaggedAlarmView.setSortFields(Arrays.asList(new SortField(sysCreatedTime, false)));
        woCreatedFlaggedAlarmView.setModuleName(FlaggedEventModule.MODULE_NAME);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("name", "Message"));
        viewFields.add(new ViewField("client", "Client"));
        viewFields.add(new ViewField("controller", "Controller"));
        viewFields.add(new ViewField(FilteredAlarmModule.FLAGGED_ALARM_PROCESS_FIELD_NAME, "Flagged Alarm Process"));
        viewFields.add(new ViewField("status", "Status"));
        viewFields.add(new ViewField("workorder", "Workorder"));

        woCreatedFlaggedAlarmView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        woCreatedFlaggedAlarmView.setAppLinkNames(appLinkNames);

        return woCreatedFlaggedAlarmView;
    }

    private static FacilioView getAssignedToMyTeamFlaggedAlarms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Criteria criteria = new Criteria();

        FacilioField teamField = modBean.getField("team", FlaggedEventModule.MODULE_NAME);
        Condition myTeamFlaggedAlarmsCondition = new Condition();
        myTeamFlaggedAlarmsCondition.setField(teamField);
        myTeamFlaggedAlarmsCondition.setOperator(TeamOperator.CURRENT_PEOPLE_IN_TEAM);
        criteria.addAndCondition(myTeamFlaggedAlarmsCondition);

        FacilioField sysCreatedTime = new FacilioField();
        sysCreatedTime.setName("sysCreatedTime");
        sysCreatedTime.setColumnName("SYS_CREATED_TIME");
        sysCreatedTime.setDataType(FieldType.NUMBER);
        sysCreatedTime.setModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));

        FacilioView assignedToMyTeamFlaggedAlarmView = new FacilioView();
        assignedToMyTeamFlaggedAlarmView.setName("myteam");
        assignedToMyTeamFlaggedAlarmView.setDisplayName("Assigned To My Team Flagged Alarms");
        assignedToMyTeamFlaggedAlarmView.setCriteria(criteria);
        assignedToMyTeamFlaggedAlarmView.setSortFields(Arrays.asList(new SortField(sysCreatedTime, false)));
        assignedToMyTeamFlaggedAlarmView.setModuleName(FlaggedEventModule.MODULE_NAME);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("name", "Message"));
        viewFields.add(new ViewField("client", "Client"));
        viewFields.add(new ViewField("controller", "Controller"));
        viewFields.add(new ViewField(FilteredAlarmModule.FLAGGED_ALARM_PROCESS_FIELD_NAME, "Flagged Alarm Process"));
        viewFields.add(new ViewField("status", "Status"));
        viewFields.add(new ViewField("workorder", "Workorder"));

        assignedToMyTeamFlaggedAlarmView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        assignedToMyTeamFlaggedAlarmView.setAppLinkNames(appLinkNames);

        return assignedToMyTeamFlaggedAlarmView;
    }

    private static FacilioView getAssignedToMeFlaggedAlarms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Criteria criteria = new Criteria();

        FacilioField assignedPeopleField = modBean.getField("assignedPeople", FlaggedEventModule.MODULE_NAME);
        Condition myFlaggedAlarmsCondition = new Condition();
        myFlaggedAlarmsCondition.setField(assignedPeopleField);
        myFlaggedAlarmsCondition.setOperator(PeopleOperator.CURRENT_USER);
        criteria.addAndCondition(myFlaggedAlarmsCondition);

        FacilioField sysCreatedTime = new FacilioField();
        sysCreatedTime.setName("sysCreatedTime");
        sysCreatedTime.setColumnName("SYS_CREATED_TIME");
        sysCreatedTime.setDataType(FieldType.NUMBER);
        sysCreatedTime.setModule(modBean.getModule(FlaggedEventModule.MODULE_NAME));

        FacilioView assignedToMeFlaggedAlarmView = new FacilioView();
        assignedToMeFlaggedAlarmView.setName("assignedtome");
        assignedToMeFlaggedAlarmView.setDisplayName("Assign To Me Flagged Alarms");
        assignedToMeFlaggedAlarmView.setCriteria(criteria);
        assignedToMeFlaggedAlarmView.setSortFields(Arrays.asList(new SortField(sysCreatedTime, false)));
        assignedToMeFlaggedAlarmView.setModuleName(FlaggedEventModule.MODULE_NAME);

        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField("name", "Message"));
        viewFields.add(new ViewField("client", "Client"));
        viewFields.add(new ViewField("controller", "Controller"));
        viewFields.add(new ViewField(FilteredAlarmModule.FLAGGED_ALARM_PROCESS_FIELD_NAME, "Flagged Alarm Process"));
        viewFields.add(new ViewField("status", "Status"));
        viewFields.add(new ViewField("workorder", "Workorder"));

        assignedToMeFlaggedAlarmView.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        assignedToMeFlaggedAlarmView.setAppLinkNames(appLinkNames);

        return assignedToMeFlaggedAlarmView;
    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields remoteMonitorApp = new ScopeVariableModulesFields();
        remoteMonitorApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_remotemonitor_client"));
        remoteMonitorApp.setModuleId(module.getModuleId());
        remoteMonitorApp.setFieldName("client");

        scopeConfigList = Arrays.asList(remoteMonitorApp);
        return scopeConfigList;
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        return getSystemPagesMap(getModuleName());
    }


    public static Map<String, List<PagesContext>> getSystemPagesMap(String moduleName) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", AddSubModuleRelations.FLAGGED_EVENT_ACTIVITY);

        ApplicationContext app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        FacilioModule module = modBean.getModule(moduleName);

        Map<String, List<PagesContext>> pageMap = new HashMap<>();

        pageMap.put(app.getLinkName(), new ModulePages()
                .addPage("defaultPage", "Default Page", "", null, true, true, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Flagged Alarm Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .sectionDone()
                .addSection("relatedAlarms", "", "")
                .addWidget("filteredAlarmsRelated", "Filtered Alarms", PageWidget.WidgetType.RELATED_LIST, "relatedListwidgetViewWidget_6_12", 0, 0, null, getSingleRelatedListForModule(modBean.getModule(FlaggedEventModule.MODULE_NAME), FilteredAlarmModule.MODULE_NAME,"flaggedAlarm"))
                .widgetDone()
                .addWidget("alarmEventRelated", "Alarm Events", PageWidget.WidgetType.RELATED_LIST, "relatedListwidgetViewWidget_6_12", 0, 0, null, getSingleRelatedListForModule(modBean.getModule(FlaggedEventModule.MODULE_NAME), RawAlarmModule.MODULE_NAME,"flaggedEvent"))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("timeLog", "Time Log", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("timeLog", null, null)
                .addWidget("flaggedAlarmTimeLog", "Flagged Alarm Time Log", PageWidget.WidgetType.FLAGGED_ALARM_TIME_LOG, "flaggedAlarmTimeLog_6_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .addPage("pageWithEvaluationDetails", "Page With Evaluation Details", "", getGageWithEvaluationDetailCriteria(), true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("summaryfields", null, null)
                .addWidget("summaryFieldsWidget", "Flagged Alarm Details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_6_9", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
                .widgetDone()
                .addWidget("evaluationTimeRemaining", "Evaluation Time Remaining", PageWidget.WidgetType.EVALUATION_TIME_REMAINING, "evaluationTimeRemaining_2_3", 9, 0, null, null)
                .widgetDone()
                .addWidget("evaluationTeamDetails", "Evaluation Team Details", PageWidget.WidgetType.EVALUATION_TEAM_DETAILS, "evaluationTeamDetails_4_3", 9, 8, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("relatedAlarms", "", "")
                .addWidget("filteredAlarmsRelated", "Filtered Alarms", PageWidget.WidgetType.RELATED_LIST, "relatedListwidgetViewWidget_6_12", 0, 0, null, getSingleRelatedListForModule(modBean.getModule(FlaggedEventModule.MODULE_NAME), FilteredAlarmModule.MODULE_NAME,"flaggedAlarm"))
                .widgetDone()
                .addWidget("alarmEventRelated", "Alarm Events", PageWidget.WidgetType.RELATED_LIST, "relatedListwidgetViewWidget_6_12", 0, 0, null, getSingleRelatedListForModule(modBean.getModule(FlaggedEventModule.MODULE_NAME), RawAlarmModule.MODULE_NAME,"flaggedEvent"))
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 4, null, getWidgetGroup())
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("timeLog", "Time Log", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("timeLog", null, null)
                .addWidget("flaggedAlarmTimeLog", "Flagged Alarm Time Log", PageWidget.WidgetType.FLAGGED_ALARM_TIME_LOG, "flaggedAlarmTimeLog_6_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("history", null, null)
                .addWidget("historyWidget", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                .pageDone()
                .getCustomPages());
        return pageMap;
    }

    public static JSONObject getSingleRelatedListForModule(FacilioModule module, String subModuleName, String fieldName) throws Exception {
        List<RelatedListWidgetContext> relatedLists = RelatedListWidgetUtil.fetchAllRelatedList(module, false, null, null);
        if(CollectionUtils.isNotEmpty(relatedLists)) {
            relatedLists.removeIf(relList -> !(subModuleName.equalsIgnoreCase(relList.getSubModuleName()) && fieldName.equalsIgnoreCase(relList.getFieldName())));
            RelatedListWidgetContext relList = CollectionUtils.isNotEmpty(relatedLists) ? relatedLists.get(0) : null;
            return FieldUtil.getAsJSON(relList);
        }
        return null;
    }

    private static JSONObject getSummaryWidgetDetails(String moduleName, ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

//        FacilioField nameField = moduleBean.getField("name",moduleName);
        FacilioField clientField = moduleBean.getField("client",moduleName);
        FacilioField siteField = moduleBean.getField("site", moduleName);
        FacilioField controllerField = moduleBean.getField("controller",moduleName);
        FacilioField assetField = moduleBean.getField("asset", moduleName);
        FacilioField workorderField = moduleBean.getField("workorder",moduleName);
        FacilioField serviceOrder = moduleBean.getField("serviceOrder",moduleName);
        FacilioField controlAction = moduleBean.getField("controlAction",moduleName);
        FacilioField closeIssueReasonField = moduleBean.getField("bureauCloseIssues",moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
//        addSummaryFieldInWidgetGroup(widgetGroup, nameField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, clientField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, siteField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, controllerField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, assetField, 1, 4, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, workorderField, 2, 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, serviceOrder, 2,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup, controlAction, 2, 3, 1);
        addSummaryFieldInWidgetGroup(widgetGroup, closeIssueReasonField, 2, 4, 1);


        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        widgetGroup.setColumns(4);
        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);



        FacilioField sysCreatedByField = moduleBean.getField("sysCreatedByPeople", moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedByPeople", moduleName);
        FacilioField sysModifiedTimeField = moduleBean.getField("sysModifiedTime", moduleName);

        SummaryWidgetGroup systemInformationGroup = new SummaryWidgetGroup();
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysCreatedByField, 1, 1, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysCreatedTimeField, 1, 2, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysModifiedByField, 1, 3, 1);
        addSummaryFieldInWidgetGroup(systemInformationGroup, sysModifiedTimeField, 1, 4, 1);


        systemInformationGroup.setName("systemInformation");
        systemInformationGroup.setDisplayName("System Information");
        systemInformationGroup.setColumns(4);
        widgetGroupList.add(systemInformationGroup);

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


    private static JSONObject getWidgetGroup() throws Exception {

        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "flaggedAlarmnotes");

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "flaggedAlarmattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, "flexiblewebcomment_5", 0, 4, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, "flexiblewebattachment_5", 0, 4, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }

    private static Criteria getGageWithEvaluationDetailCriteria() throws Exception {
        Criteria criteria = new Criteria();
        Condition currentTeamCondition = CriteriaAPI.getCondition("team","TEAM_ID", TeamOperator.CURRENT_PEOPLE_IN_TEAM);
        criteria.addAndCondition(currentTeamCondition);
        return  criteria;
    }
}
