package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.NewAlarmAPI;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.*;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

import java.util.*;

import static com.facilio.connected.CommonConnectedSummaryAPI.getReadingAlarmSystemPage;


public class NewReadingAlarmModule extends BaseModuleConfig{
    public NewReadingAlarmModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.NEW_READING_ALARM);
    }

    @Override
    public void addData() throws Exception {
        addSystemButtonsForReadingAlarm();
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> fddAlarms = new ArrayList<FacilioView>();
        fddAlarms.add(getAllReadingAlarmViews().setOrder(order++));
        fddAlarms.add(getReadingAlarmSeverity("active", "Active", FacilioConstants.Alarm.CLEAR_SEVERITY, false).setOrder(order++));
        fddAlarms.add(getReadingAlarmUnacknowledged().setOrder(order++));
        fddAlarms.add(getReadingAlarmSeverity("critical", "Critical Faults", "Critical", true).setOrder(order++));
        fddAlarms.add(getReadingAlarmSeverity("major", "Major Faults", "Major", true).setOrder(order++));
        fddAlarms.add(getReadingAlarmSeverity("minor", "Minor Faults", "Minor", true).setOrder(order++));
        fddAlarms.add(getReadingAlarmSeverity("cleared", "Cleared Faults", FacilioConstants.Alarm.CLEAR_SEVERITY, true).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "fddAlarmsViews");
        groupDetails.put("displayName", "FDD Alarms");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.NEW_READING_ALARM);
        groupDetails.put("views", fddAlarms);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllReadingAlarmViews() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());

        Criteria criteria = new Criteria();
        Condition tillDateAlarm = getOnlyTillDateAlarm();
        criteria.addAndCondition(tillDateAlarm);

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Faults");
        allView.setModuleName("newreadingalarm");
        allView.setCriteria(criteria);
        allView.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getReadingAlarmSeverity(String name, String displayName, String severity, boolean equals) {

        Condition alarmCondition = getReadingAlarmSeverityCondition(severity, equals);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(alarmCondition);
        Condition tillDateAlarm = getOnlyTillDateAlarm();
        criteria.addAndCondition(tillDateAlarm);

        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());

        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setCriteria(criteria);
        view.setModuleName("newreadingalarm");
        view.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }

    private static FacilioView getReadingAlarmUnacknowledged() {
        Criteria criteria = getReadingAlarmUnacknowledgedCriteria();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());
        Condition tillDateAlarm = getOnlyTillDateAlarm();
        criteria.addAndCondition(tillDateAlarm);

        FacilioView typeAlarms = new FacilioView();
        typeAlarms.setName("unacknowledged");
        typeAlarms.setDisplayName("Unacknowledged");
        typeAlarms.setCriteria(criteria);
        typeAlarms.setModuleName("newreadingalarm");
        typeAlarms.setSortFields(Arrays.asList(new SortField(createdTime, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        typeAlarms.setAppLinkNames(appLinkNames);

        return typeAlarms;
    }

    public static Condition getOnlyTillDateAlarm() {
        FacilioField createdTime = new FacilioField();
        createdTime.setName("lastOccurredTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setColumnName("LAST_OCCURRED_TIME");
        createdTime.setModule(ModuleFactory.getBaseAlarmModule());


        Condition alarmCondition = new Condition();
        alarmCondition.setField(createdTime);
        alarmCondition.setOperator(DateOperators.TILL_NOW);

        return alarmCondition;
    }
    public static Condition getReadingAlarmSeverityCondition(String severity, boolean equals) {
        LookupField severityField = new LookupField();
        severityField.setName("severity");
        severityField.setColumnName("SEVERITY");
        severityField.setDataType(FieldType.LOOKUP);
        severityField.setModule(ModuleFactory.getBaseAlarmModule());
        severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

        Condition alarmCondition = new Condition();
        alarmCondition.setField(severityField);
        alarmCondition.setOperator(LookupOperator.LOOKUP);
        alarmCondition.setCriteriaValue(getSeverityAlarmCriteria(severity, equals));

        return alarmCondition;
    }

    public static Criteria getReadingAlarmUnacknowledgedCriteria() {
        Condition falseCondition = new Condition();
        falseCondition.setColumnName("ACKNOWLEDGED");
        falseCondition.setFieldName("acknowledged");
        falseCondition.setOperator(BooleanOperators.IS);
        falseCondition.setValue(String.valueOf(false));

        Condition emptyCondition = new Condition();
        emptyCondition.setColumnName("ACKNOWLEDGED");
        emptyCondition.setFieldName("acknowledged");
        emptyCondition.setOperator(CommonOperators.IS_EMPTY);

        Criteria criteria = new Criteria();
        criteria.addOrCondition(emptyCondition);
        criteria.addOrCondition(falseCondition);

        LookupField severityField = new LookupField();
        severityField.setName("severity");
        severityField.setColumnName("SEVERITY");
        severityField.setDataType(FieldType.LOOKUP);
        severityField.setModule(ModuleFactory.getBaseAlarmModule());
        severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());

        Condition activeAlarm = new Condition();
        activeAlarm.setField(severityField);
        activeAlarm.setOperator(LookupOperator.LOOKUP);
        activeAlarm.setCriteriaValue(getSeverityAlarmCriteria("Clear", false));

        criteria.addAndCondition(activeAlarm);
        return criteria;
    }

    private static Criteria getSeverityAlarmCriteria(String severity, boolean equals) {
        FacilioField severityField = new FacilioField();
        severityField.setName("severity");
        severityField.setColumnName("SEVERITY");
        severityField.setDataType(FieldType.STRING);
        severityField.setModule(ModuleFactory.getAlarmSeverityModule());

        Condition activeAlarm = new Condition();
        activeAlarm.setField(severityField);
        if (equals) {
            activeAlarm.setOperator(StringOperators.IS);
        } else {
            activeAlarm.setOperator(StringOperators.ISN_T);
        }
        activeAlarm.setValue(severity);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(activeAlarm);

        return criteria;
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();

        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);

        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,getReadingAlarmSystemPage(app, false,  true));
        }
        return appNameVsPage;
    }

    public static void addSystemButtonsForReadingAlarm() throws Exception {
        addAcknowledgeButton();
        addCreateWorkOrderButton();
        addViewWorkOrderButton();
    }

    public static void addAcknowledgeButton() throws Exception {
        //Reading Alarm Acknowledge Button
        SystemButtonRuleContext acknowledgeAlarmButton = NewAlarmAPI.getAcknowledgeAlarmSystemButton();
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.NEW_READING_ALARM, acknowledgeAlarmButton);
    }

    public static void addCreateWorkOrderButton() throws Exception {
        //Reading Alarm Create Workorder Button
        SystemButtonRuleContext createWorkorderSystemButton = NewAlarmAPI.getCreateWoSystemButton();
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.NEW_READING_ALARM,createWorkorderSystemButton);
    }
    public static void addViewWorkOrderButton() throws Exception {
        //Reading Alarm View Workorder Button
        SystemButtonRuleContext viewWorkorderSystemButton = NewAlarmAPI.getViewWoSystemButton();
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.NEW_READING_ALARM,viewWorkorderSystemButton);
    }
}
